package com.me.gestureGym.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class DataWrapper {
	
	private static final String CRLF = "\r\n";
	
	private static class FileAndTimestamp implements Comparable<FileAndTimestamp> {
		public FileHandle _file;
		public long _lastModified;
		
		public FileAndTimestamp(FileHandle file) {
			_file = file;
			_lastModified = file.lastModified();
		}

		@Override
		/* 
		 * Ensures they get sorted from newest to oldest
		 */
		public int compareTo(FileAndTimestamp other) {
			return -1 * Long.compare(_lastModified, other._lastModified);
		}
	}
	
	private static ZoneResponseInfo[] defaultData() {
		ZoneResponseInfo[] out = new ZoneResponseInfo[16];
		for (int i = 0; i < 16; i++) {
			out[i] = new ZoneResponseInfo(i, 2.0f, 1.0);
		}
		return out;
	}
	
	private static HistoricalZoneResponseInfo[] defaultHistoricalData() {
		HistoricalZoneResponseInfo[] out = new HistoricalZoneResponseInfo[16];
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		String dateString = df.format(cal.getTime());
		
		for (int i = 0; i < 16; i++) {
			out[i] = new HistoricalZoneResponseInfo(i, 2.0f, 1.0, dateString);
		}
		return out;
	}
	
	private static ZoneResponseInfo[] fileToZoneResponses(FileHandle f) 
			throws IOException, ImproperFileFormatException {
		BufferedReader r = new BufferedReader(f.reader());
		ZoneResponseInfo[] out = new ZoneResponseInfo[16];
		for (int i = 0; i < 16; i++) {
			String[] line = r.readLine().split(",");
			if (line.length != 3) throw new ImproperFileFormatException();
			out[i] = new ZoneResponseInfo(Integer.parseInt(line[0]), 
					                      Float.parseFloat(line[1]),
					                      Double.parseDouble(line[2]));
		}
		
		r.close();
		
		return out;
	}
	
	private static HistoricalZoneResponseInfo[] fileToHistoricalZoneResponses(FileHandle f) 
			throws IOException, ImproperFileFormatException {
		BufferedReader r = new BufferedReader(f.reader());
		HistoricalZoneResponseInfo[] out = new HistoricalZoneResponseInfo[16];
		for (int i = 0; i < 16; i++) {
			String[] line = r.readLine().split(",");
			if (line.length != 3) throw new ImproperFileFormatException();
			out[i] = new HistoricalZoneResponseInfo(Integer.parseInt(line[0]), 
					                                Float.parseFloat(line[1]),
					                                Double.parseDouble(line[2]),
					                                f.toString().substring(f.toString().lastIndexOf('/') + 1));
		}
		
		r.close();
		
		return out;
	}
	
	public static String getCurrentPatient() throws LocalStorageDoesNotExistException, IOException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		FileHandle cpFile = Gdx.files.local("data/currentpatient.txt");
		if (!cpFile.exists()) {
			System.out.println("lolwut...");
			return null;
		}
		
		BufferedReader r = new BufferedReader(cpFile.reader());
		String out = r.readLine();
		System.out.println("WE READ: " + out);
		r.close();
		
		return out;
	}
	
	public static void setCurrentPatient(String patientName) throws LocalStorageDoesNotExistException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		FileHandle cpFile = Gdx.files.local("data/currentpatient.txt");
		if(cpFile.exists()){
			System.out.println("Found it...");
			cpFile.delete();
			cpFile.writeString(patientName + CRLF, true);
		}
		else{
			System.out.println("nope...");
			cpFile.writeString(patientName + CRLF, true);
		}
	}
	
	public static boolean putPatient(String patientName, String doctorName) 
			throws IOException, LocalStorageDoesNotExistException, NoSuchDoctorException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		// put patient in patients file
		FileHandle patientsFile = Gdx.files.local("data/patients.txt");
		if (patientsFile.exists()) {
			BufferedReader r = new BufferedReader(patientsFile.reader());
			while (r.ready()) {
				String user = r.readLine();
				if (user.equals(patientName)) {
					return false;
				}
			}
			
			r.close();
		}
		
		patientsFile.writeString(patientName + CRLF, true);
		
		if (doctorName == null) return true;
		
		// add patient to list of patients that a doctor has
		FileHandle doctorsFile = Gdx.files.local("data/doctors.txt");
		StringBuilder doctorsFileContents = new StringBuilder();
		BufferedReader r = new BufferedReader(doctorsFile.reader());
		
		boolean doctorNameFound = false;
		
		while (r.ready()) {
			String doctorString = r.readLine();
			String thisDoctorName = doctorString.substring(doctorString.indexOf(','));
			if (thisDoctorName.equals(doctorName)) {
				doctorNameFound = true;
				doctorsFileContents.append(doctorString + "," + patientName + CRLF);
			} else {
				doctorsFileContents.append(doctorString + CRLF);
			}
		}
		
		r.close();
		
		if (!doctorNameFound) throw new NoSuchDoctorException();
		
		doctorsFile.delete();
		doctorsFile.writeString(doctorsFileContents.toString(), true);
		
		return true;
	}
	
	public static ArrayList<String> getAllPatients() 
			throws LocalStorageDoesNotExistException, IOException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		ArrayList<String> out = new ArrayList<String>();
		
		FileHandle patientsFile = Gdx.files.local("data/patients.txt");
		if (!patientsFile.exists()) return out;
		BufferedReader r = new BufferedReader(patientsFile.reader());
		while (r.ready()) {
			out.add(r.readLine());
		}
		
		Collections.sort(out);
		
		return out;
	}
	
	public static void putSingleTouchData(String patientName, ZoneResponseInfo[] data) 
			throws LocalStorageDoesNotExistException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		String dateString = df.format(cal.getTime());
		
		FileHandle dataFile = Gdx.files.local("data/" + patientName + "/st/" + dateString);
		
		if (dataFile.exists()) {
			dataFile.delete();
		}
		
		for (int i = 0; i < data.length; i++) {
			dataFile.writeString(data[i].getZoneNumber() + "," + 
		                         data[i].getSuccessDuration() + "," +
					             data[i].getHitRate() + CRLF, 
					             true);
		}
	}
	
	public static ZoneResponseInfo[] getMostRecentSingleTouchData(String patientName) 
			throws LocalStorageDoesNotExistException, IOException, ImproperFileFormatException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		FileHandle stDir = Gdx.files.local("data/" + patientName + "/st");
		if (!stDir.exists()) return defaultData();
		
		FileHandle[] stDatas = stDir.list();
		
		int fileIndex = -1;
		long lastModified = Long.MIN_VALUE;
		for (int i = 0; i < stDatas.length; i++) {
			if (stDatas[i].lastModified() > lastModified) {
				lastModified = stDatas[i].lastModified();
				fileIndex = i;
			}
		}
		
		FileHandle mostRecentFile = stDatas[fileIndex];
		
		return fileToZoneResponses(mostRecentFile);
	}
	
	/*
	 * ArrayList is sorted from newest data to oldest data
	 */
	public static ArrayList<HistoricalZoneResponseInfo[]> getAllSingleTouchData(String patientName) 
			throws LocalStorageDoesNotExistException, IOException, ImproperFileFormatException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		ArrayList<HistoricalZoneResponseInfo[]> out = new ArrayList<HistoricalZoneResponseInfo[]>();
		
		FileHandle stDir = Gdx.files.local("data/" + patientName + "/st");
		if (!stDir.exists()) {
			out.add(defaultHistoricalData());
			return out;
		}
		
		FileHandle[] files = stDir.list();
		FileAndTimestamp[] toSort = new FileAndTimestamp[files.length];
		for (int i = 0; i < files.length; i++) {
			toSort[i] = new FileAndTimestamp(files[i]);
		}
		Arrays.sort(toSort);
		
		for (int i = 0; i < toSort.length; i++) {
			out.add(fileToHistoricalZoneResponses(toSort[i]._file));
		}
		
		return out;
	}
	
	public static void putMultiTouchData(String patientName, ZoneResponseInfo[] data) 
			throws LocalStorageDoesNotExistException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		String dateString = df.format(cal.getTime());
		
		FileHandle dataFile = Gdx.files.local("data/" + patientName + "/mt/" + dateString);
		
		if (dataFile.exists()) {
			dataFile.delete();
		}
		
		for (int i = 0; i < data.length; i++) {
			dataFile.writeString(data[i].getZoneNumber() + "," + 
		                         data[i].getSuccessDuration() + "," +
					             data[i].getHitRate() + CRLF, 
					             true);
		}
	}
	
	public static ZoneResponseInfo[] getMostRecentMultiTouchData(String patientName) 
			throws LocalStorageDoesNotExistException, IOException, ImproperFileFormatException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		FileHandle mtDir = Gdx.files.local("data/" + patientName + "/mt");
		if (!mtDir.exists()) return defaultData();
		
		FileHandle[] mtDatas = mtDir.list();
		
		int fileIndex = -1;
		long lastModified = Long.MIN_VALUE;
		for (int i = 0; i < mtDatas.length; i++) {
			if (mtDatas[i].lastModified() > lastModified) {
				lastModified = mtDatas[i].lastModified();
				fileIndex = i;
			}
		}
		
		FileHandle mostRecentFile = mtDatas[fileIndex];
		return fileToZoneResponses(mostRecentFile);
	}
	
	/*
	 * ArrayList is sorted from newest data to oldest data
	 */
	public static ArrayList<HistoricalZoneResponseInfo[]> getAllMultiTouchData(String patientName) 
			throws LocalStorageDoesNotExistException, IOException, ImproperFileFormatException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		ArrayList<HistoricalZoneResponseInfo[]> out = new ArrayList<HistoricalZoneResponseInfo[]>();
		
		FileHandle stDir = Gdx.files.local("data/" + patientName + "/mt");
		if (!stDir.exists()) {
			out.add(defaultHistoricalData());
			return out;
		}
		
		FileHandle[] files = stDir.list();
		FileAndTimestamp[] toSort = new FileAndTimestamp[files.length];
		Arrays.sort(toSort);
		
		for (int i = 0; i < toSort.length; i++) {
			out.add(fileToHistoricalZoneResponses(toSort[i]._file));
		}
		
		return out;
	}
	
	public static boolean putDoctor(String doctorName, String password) 
			throws IOException, LocalStorageDoesNotExistException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		// put doctor in doctors file
		FileHandle doctorsFile = Gdx.files.local("data/doctors.txt");
		BufferedReader r = new BufferedReader(doctorsFile.reader());
		while (r.ready()) {
			String[] line = r.readLine().split(",");
			if (line.length >= 1 && line[0].equals(doctorName)) {
				return false;
			}
		}
		
		r.close();
		
		doctorsFile.writeString(doctorName + "," + password + CRLF, true);
		
		return true;
	}
	
	public static boolean validateDoctor(String doctorName, String password) 
			throws LocalStorageDoesNotExistException, IOException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		FileHandle doctorsFile = Gdx.files.local("data/doctors.txt");
		BufferedReader r = new BufferedReader(doctorsFile.reader());
		while (r.ready()) {
			String[] line = r.readLine().split(",");
			if (line.length >= 2 && line[0].equals(doctorName) && line[1].equals(password)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static String[] getPatientsOfDoctor(String doctorName) 
			throws LocalStorageDoesNotExistException, IOException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		FileHandle doctorsFile = Gdx.files.local("data/doctors.txt");
		BufferedReader r = new BufferedReader(doctorsFile.reader());
		while (r.ready()) {
			String[] line = r.readLine().split(",");
			if (line.length > 2 && line[0].equals(doctorName)) {
				String[] out = new String[line.length - 2];
				for (int i = 2; i < line.length; i++) {
					out[i - 2] = line[i];
				}
				return out;
			}
		}
		
		return null;
	}
	
	public static void writeHardcodedHeatmaps() throws LocalStorageDoesNotExistException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		// TODO: Finish this
		
	}
}

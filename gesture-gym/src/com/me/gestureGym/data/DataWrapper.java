package com.me.gestureGym.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class DataWrapper {
	
	private static final String CRLF = "\r\n";
	
	private static class FileAndTimestamp implements Comparable<FileAndTimestamp> {
		public FileHandle _file;
		public Date _date;
		
		public FileAndTimestamp(FileHandle file) throws ParseException {
			_file = file;
			_date = new SimpleDateFormat("yyyy-MM-dd").parse(file.name());
		}

		@Override
		/* 
		 * Ensures they get sorted from newest to oldest
		 */
		public int compareTo(FileAndTimestamp other) {
			return _date.after(other._date) ? -1 : 1;
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
			throws LocalStorageDoesNotExistException, IOException, ImproperFileFormatException, ParseException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		FileHandle stDir = Gdx.files.local("data/" + patientName + "/st");
		if (!stDir.exists()) return defaultData();
		
		FileHandle[] stDatas = stDir.list();
		
		int fileIndex = -1;
		Date latestDate = null;
		for (int i = 0; i < stDatas.length; i++) {
			Date d = new SimpleDateFormat("yyyy-MM-dd").parse(stDatas[i].name());
			if (latestDate == null) {
				latestDate = d;
				fileIndex = i;
			} else {
				if (d.after(latestDate)) {
					latestDate = d;
					fileIndex = i;
				}
			}
		}
		
		FileHandle mostRecentFile = stDatas[fileIndex];
		
		return fileToZoneResponses(mostRecentFile);
	}
	
	/*
	 * ArrayList is sorted from newest data to oldest data
	 */
	public static ArrayList<HistoricalZoneResponseInfo[]> getAllSingleTouchData(String patientName) 
			throws LocalStorageDoesNotExistException, IOException, ImproperFileFormatException, ParseException {
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
			throws LocalStorageDoesNotExistException, IOException, ImproperFileFormatException, ParseException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		FileHandle mtDir = Gdx.files.local("data/" + patientName + "/mt");
		if (!mtDir.exists()) return defaultData();
		
		FileHandle[] mtDatas = mtDir.list();
		
		int fileIndex = -1;
		Date latestDate = null;
		for (int i = 0; i < mtDatas.length; i++) {
			Date d = new SimpleDateFormat("yyyy-MM-dd").parse(mtDatas[i].name());
			if (latestDate == null) {
				latestDate = d;
				fileIndex = i;
			} else {
				if (d.after(latestDate)) {
					latestDate = d;
					fileIndex = i;
				}
			}
		}
		
		FileHandle mostRecentFile = mtDatas[fileIndex];
		return fileToZoneResponses(mostRecentFile);
	}
	
	/*
	 * ArrayList is sorted from newest data to oldest data
	 */
	public static ArrayList<HistoricalZoneResponseInfo[]> getAllMultiTouchData(String patientName) 
			throws LocalStorageDoesNotExistException, IOException, ImproperFileFormatException, ParseException {
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
		for (int i = 0; i < files.length; i++) {
			toSort[i] = new FileAndTimestamp(files[i]);
		}
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
	
	public static void writeHardcodedHeatmaps() throws LocalStorageDoesNotExistException, IOException, NoSuchDoctorException {
		boolean isLocalStorageAvailable = Gdx.files.isLocalStorageAvailable();
		if (!isLocalStorageAvailable) {
			throw new LocalStorageDoesNotExistException();
		}
		
		// TODO: Finish this
		putPatient("Tony Parker", null);
		FileHandle f = Gdx.files.local("data/Tony Parker/mt/2014-04-10");
		f.delete();
		f.writeString("0,1.7472783,1.0" + CRLF, true);
		f.writeString("1,1.856894,1.0" + CRLF, true);
		f.writeString("2,1.780549,1.0" + CRLF, true);
		f.writeString("3,1.8467978,0.95" + CRLF, true);
		f.writeString("4,1.740549,1.0" + CRLF, true);
		f.writeString("5,1.8267978,0.95" + CRLF, true);
		f.writeString("6,1.710549,1.0" + CRLF, true);
		f.writeString("7,1.616894,1.0" + CRLF, true);
		f.writeString("8,1.686894,1.0" + CRLF, true);
		f.writeString("9,1.6472783,1.0" + CRLF, true);
		f.writeString("10,1.6472783,1.0" + CRLF, true);
		f.writeString("11,1.806894,1.0" + CRLF, true);
		f.writeString("12,1.806894,1.0" + CRLF, true);
		f.writeString("13,1.710549,1.0" + CRLF, true);
		f.writeString("14,1.806894,1.0" + CRLF, true);
		f.writeString("15,2.0,1.0" + CRLF, true);

		f = Gdx.files.local("data/Tony Parker/mt/2014-04-11");
		f.delete();
		f.writeString("0,1.7272783,1.0" + CRLF, true);
		f.writeString("1,1.856894,1.0" + CRLF, true);
		f.writeString("2,1.780549,1.0" + CRLF, true);
		f.writeString("3,1.7467978,0.95" + CRLF, true);
		f.writeString("4,1.740549,1.0" + CRLF, true);
		f.writeString("5,1.8267978,0.95" + CRLF, true);
		f.writeString("6,1.680549,1.0" + CRLF, true);
		f.writeString("7,1.616894,1.0" + CRLF, true);
		f.writeString("8,1.686894,1.0" + CRLF, true);
		appendLineToFile(f, "9,1.6472783,1.0");
		appendLineToFile(f, "10,1.6472783,1.0");
		appendLineToFile(f, "11,1.706894,1.0");
		appendLineToFile(f, "12,1.806894,1.0");
		appendLineToFile(f, "13,1.710549,1.0");
		appendLineToFile(f, "14,1.806894,1.0");
		appendLineToFile(f, "15,2.0,1.0");
		
		f = Gdx.files.local("data/Tony Parker/mt/2014-04-12");
		appendLineToFile(f, "0,1.6473288,1.0");
		appendLineToFile(f, "1,1.770756,0.95");
		appendLineToFile(f, "2,1.6473288,0.95");
		appendLineToFile(f, "3,1.740948,1.0");
		appendLineToFile(f, "4,1.680948,1.0");
		appendLineToFile(f, "5,1.6473288,1.0");
		appendLineToFile(f, "6,1.740948,1.0");
		appendLineToFile(f, "7,1.770756,0.95");
		appendLineToFile(f, "8,1.680948,0.95");
		appendLineToFile(f, "9,1.6473288,1.0");
		appendLineToFile(f, "10,1.7473288,1.0");
		appendLineToFile(f, "11,1.770756,0.95");
		appendLineToFile(f, "12,1.770756,1.0");
		appendLineToFile(f, "13,1.7126982,1.0");
		appendLineToFile(f, "14,1.7473288,0.7142857142857143");
		appendLineToFile(f, "15,1.816894,1.0");

		f = Gdx.files.local("data/Tony Parker/st/2014-04-10");
		f.delete();
		appendLineToFile(f, "0,1.6472783,1.0");
		appendLineToFile(f, "1,1.806894,1.0");
		appendLineToFile(f, "2,1.680549,1.0");
		appendLineToFile(f, "3,1.8467978,0.95");
		appendLineToFile(f, "4,1.680549,1.0");
		appendLineToFile(f, "5,1.8467978,0.95");
		appendLineToFile(f, "6,1.680549,1.0");
		appendLineToFile(f, "7,1.806894,1.0");
		appendLineToFile(f, "8,1.806894,1.0");
		appendLineToFile(f, "9,1.6472783,1.0");
		appendLineToFile(f, "10,1.6472783,1.0");
		appendLineToFile(f, "11,1.806894,1.0");
		appendLineToFile(f, "12,1.806894,1.0");
		appendLineToFile(f, "13,1.680549,1.0");
		appendLineToFile(f, "14,1.806894,1.0");
		appendLineToFile(f, "15,1.806894,1.0");
		
		f = Gdx.files.local("data/Tony Parker/st/2014-04-11");
		f.delete();
		appendLineToFile(f, "0,1.6072783,1.0");
		appendLineToFile(f, "1,1.706894,1.0");
		appendLineToFile(f, "2,1.680549,1.0");
		appendLineToFile(f, "3,1.7467978,0.95");
		appendLineToFile(f, "4,1.640549,1.0");
		appendLineToFile(f, "5,1.8267978,0.95");
		appendLineToFile(f, "6,1.630549,1.0");
		appendLineToFile(f, "7,1.746894,1.0");
		appendLineToFile(f, "8,1.726894,1.0");
		appendLineToFile(f, "9,1.6472783,1.0");
		appendLineToFile(f, "10,1.6472783,1.0");
		appendLineToFile(f, "11,1.706894,1.0");
		appendLineToFile(f, "12,1.806894,1.0");
		appendLineToFile(f, "13,1.680549,1.0");
		appendLineToFile(f, "14,1.706894,1.0");
		appendLineToFile(f, "15,1.806894,1.0");
		
		f = Gdx.files.local("data/Tony Parker/st/2014-04-12");
		f.delete();
		appendLineToFile(f, "0,1.4473288,1.0");
		appendLineToFile(f, "1,1.570756,0.95");
		appendLineToFile(f, "2,1.4473288,0.95");
		appendLineToFile(f, "3,1.540948,1.0");
		appendLineToFile(f, "4,1.480948,1.0");
		appendLineToFile(f, "5,1.4473288,1.0");
		appendLineToFile(f, "6,1.540948,1.0");
		appendLineToFile(f, "7,1.570756,0.95");
		appendLineToFile(f, "8,1.480948,0.95");
		appendLineToFile(f, "9,1.4473288,1.0");
		appendLineToFile(f, "10,1.5473288,1.0");
		appendLineToFile(f, "11,1.570756,0.95");
		appendLineToFile(f, "12,1.570756,1.0");
		appendLineToFile(f, "13,1.5126982,1.0");
		appendLineToFile(f, "14,1.6473288,0.7142857142857143");
		appendLineToFile(f, "15,1.816894,1.0");
		
		f = Gdx.files.local("data/Tony Parker/st/2014-04-14");
		f.delete();
		appendLineToFile(f, "0,1.1407307,1.0");
		appendLineToFile(f, "1,1.14,1.0");
		appendLineToFile(f, "2,1.2640348,0.95");
		appendLineToFile(f, "3,1.440948,1.0");
		appendLineToFile(f, "4,1.2678378,1.0");
		appendLineToFile(f, "5,1.2679842,1.0");
		appendLineToFile(f, "6,1.4473288,1.0");
		appendLineToFile(f, "7,1.4473288,1.0");
		appendLineToFile(f, "8,1.3407307,1.0");
		appendLineToFile(f, "9,1.3679842,1.0");
		appendLineToFile(f, "10,1.4739784,0.95");
		appendLineToFile(f, "11,1.570756,0.95");
		appendLineToFile(f, "12,1.3407307,1.0");
		appendLineToFile(f, "13,1.3640348,0.95");
		appendLineToFile(f, "14,1.4739784,0.95");
		appendLineToFile(f, "15,1.716894,1.0");

		f = Gdx.files.local("data/Tony Parker/st/2014-04-15");
		f.delete();
		appendLineToFile(f, "0,0.8007307,1.0");
		appendLineToFile(f, "1,0.81,1.0");
		appendLineToFile(f, "2,0.840347,0.95");
		appendLineToFile(f, "3,0.833288,1.0");
		appendLineToFile(f, "4,0.8232865,1.0");
		appendLineToFile(f, "5,0.8132865,1.0");
		appendLineToFile(f, "6,0.8773288,1.0");
		appendLineToFile(f, "7,1.040948,1.0");
		appendLineToFile(f, "8,0.9407307,1.0");
		appendLineToFile(f, "9,1.1679842,1.0");
		appendLineToFile(f, "10,1.2473288,1.0");
		appendLineToFile(f, "11,1.470756,0.95");
		appendLineToFile(f, "12,1.2407307,1.0");
		appendLineToFile(f, "13,1.3640348,0.95");
		appendLineToFile(f, "14,1.4739784,0.95");
		appendLineToFile(f, "15,1.5716892,1.0");

		f = Gdx.files.local("data/Tony Parker/st/2014-04-16");
		f.delete();
		appendLineToFile(f, "0,0.8007307,1.0");
		appendLineToFile(f, "1,0.81,1.0");
		appendLineToFile(f, "2,0.840347,0.95");
		appendLineToFile(f, "3,0.833288,1.0");
		appendLineToFile(f, "4,0.8232865,1.0");
		appendLineToFile(f, "5,0.8132865,1.0");
		appendLineToFile(f, "6,0.8773288,1.0");
		appendLineToFile(f, "7,1.040948,1.0");
		appendLineToFile(f, "8,0.9407307,1.0");
		appendLineToFile(f, "9,1.1679842,1.0");
		appendLineToFile(f, "10,1.2473288,1.0");
		appendLineToFile(f, "11,1.470756,0.95");
		appendLineToFile(f, "12,1.2407307,1.0");
		appendLineToFile(f, "13,1.3640348,0.95");
		appendLineToFile(f, "14,1.4739784,0.95");
		appendLineToFile(f, "15,1.5716892,1.0");

		f = Gdx.files.local("data/Tony Parker/st/2014-04-18");
		f.delete();
		appendLineToFile(f, "0,0.6507307,1.0");
		appendLineToFile(f, "1,0.68,1.0");
		appendLineToFile(f, "2,0.710347,0.95");
		appendLineToFile(f, "3,0.87288,1.0");
		appendLineToFile(f, "4,0.6132865,1.0");
		appendLineToFile(f, "5,0.6032865,1.0");
		appendLineToFile(f, "6,0.6673288,1.0");
		appendLineToFile(f, "7,0.850948,1.0");
		appendLineToFile(f, "8,0.7607307,1.0");
		appendLineToFile(f, "9,0.79842,1.0");
		appendLineToFile(f, "10,0.8873288,1.0");
		appendLineToFile(f, "11,0.920756,0.95");
		appendLineToFile(f, "12,0.8407307,1.0");
		appendLineToFile(f, "13,0.9640348,0.95");
		appendLineToFile(f, "14,1.1239784,0.95");
		appendLineToFile(f, "15,1.3516892,1.0");
	}
	
	private static void appendLineToFile(FileHandle f, String s) {
		f.writeString(s + CRLF, true);
	}
}

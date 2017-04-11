package gfiles.test;

import java.io.File;
import java.io.IOException;

import gfiles.file.VirtualFile;
import gfiles.text.xml.XMLFile;
import gfiles.text.xml.XMLFileReader;

public class Tester {
	public static void main(String[] args) {
		try {
			XMLFile xml = new XMLFile(VirtualFile.load(new File("I:/test.xml")));

			XMLFileReader read = new XMLFileReader(xml);

			XMLFile.saveTagAsFile(read.getRoot());

			System.out.println(read.getRoot());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
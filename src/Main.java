import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;

public class Main 
{
	public static Model model;
	public static ArrayList <String> medicationNameArr = new ArrayList<String>();
	public static ArrayList <Date> startDateArr = new ArrayList<Date>();
	public static ArrayList <Date> endDateArr = new ArrayList<Date>();

	public static void main(String[] args) throws ParseException 
	{
		model = ModelFactory.createDefaultModel();
		InputStream in = RDFDataMgr.open("assignment1.owl");		
		if (in == null) 
		{
			throw new IllegalArgumentException("File: not found");
		}
		model.read(in, null);
		retrivePatientData();
		retrivePatientData2();
	} 
	public static void retrivePatientData () throws ParseException   
	{
		Scanner myObj = new Scanner(System.in);
		System.out.println("Enter patient name");
		String patientName = myObj.nextLine();
		String resourceURL="http://www.semanticweb.org/dell/ontologies/2021/11/untitled-ontology-6#"+patientName;
	    Resource patient = model.getResource(resourceURL);
	    //Medication Property
		String contURL="http://www.semanticweb.org/dell/ontologies/2021/11/untitled-ontology-6#Take_Medication";
		Property containsroperty=model.createProperty(contURL);
		
				
		StmtIterator iterMedication = patient.listProperties(containsroperty);
		System.out.println("Medication: ");
		while(iterMedication.hasNext())
		{	
			String medication = "" + iterMedication.nextStatement().getObject();
			String[] arrOfStr = medication.split("#", 2);
			medicationNameArr.add(arrOfStr[1]);
			System.out.println(arrOfStr[1]);
		}
		
		//Disease Property
		String productnameURL="http://www.semanticweb.org/dell/ontologies/2021/11/untitled-ontology-6#Has_Disease";
		Property productNameroperty=model.createProperty(productnameURL);
		
		StmtIterator iterDisease = patient.listProperties(productNameroperty);
		System.out.println('\n' + "Disease: ");
		while(iterDisease.hasNext())
		{
			String disease = " " + iterDisease.nextStatement().getObject();
			String[] arrOfStr = disease.split("#", 2);
			System.out.println(arrOfStr[1]);
		}
		System.out.println('\n');
		
		
	} 
	public static void retrivePatientData2 () throws ParseException   
	{
		for(int i=0; i<medicationNameArr.size();i++)
		{
			Resource med = model.getResource("http://www.semanticweb.org/dell/ontologies/2021/11/untitled-ontology-6#"+medicationNameArr.get(i));
			String startDate="http://www.semanticweb.org/dell/ontologies/2021/11/untitled-ontology-6#Has-Start-Date"; 
			Property startDateProperty =model.createProperty(startDate);	
			StmtIterator iterStart = med.listProperties(startDateProperty);
			while(iterStart.hasNext())
			{
				String start = "" + iterStart.nextStatement().getObject();
				String[] arrOfStr = start.split("#", 2);
				Date date=new SimpleDateFormat("dd-MM-yyyy").parse(arrOfStr[1]);  
				startDateArr.add(date);
			}
			String endDate="http://www.semanticweb.org/dell/ontologies/2021/11/untitled-ontology-6#Has-End-Date"; 
			Property endDateProperty =model.createProperty(endDate);	
			StmtIterator iterEnd = med.listProperties(endDateProperty);
			while(iterEnd.hasNext())
			{
				String end = "" + iterEnd.nextStatement().getObject();
				String[] arrOfStr = end.split("#", 2);
				Date date=new SimpleDateFormat("dd-MM-yyyy").parse(arrOfStr[1]);  
				endDateArr.add(date);
			}
		}
		String proper1="http://www.semanticweb.org/dell/ontologies/2021/11/untitled-ontology-6#Major"; 
		Property property1 =model.createProperty(proper1);
		String proper2="http://www.semanticweb.org/dell/ontologies/2021/11/untitled-ontology-6#Minor"; 
		Property property2 =model.createProperty(proper2);
		String proper3="http://www.semanticweb.org/dell/ontologies/2021/11/untitled-ontology-6#Moderate"; 
		Property property3 =model.createProperty(proper3);
		for(int i=0; i<medicationNameArr.size();i++)
		{
			if (i+1<medicationNameArr.size())
			{
				if (startDateArr.get(i).compareTo(endDateArr.get(i+1))<=0 && startDateArr.get(i+1).compareTo(endDateArr.get(i))<=0)
				{
					System.out.println("ALERT!! " + medicationNameArr.get(i) +" "+ medicationNameArr.get(i+1));
					Resource med1 = model.getResource("http://www.semanticweb.org/dell/ontologies/2021/11/untitled-ontology-6#"+medicationNameArr.get(i));
					Resource med2 = model.getResource("http://www.semanticweb.org/dell/ontologies/2021/11/untitled-ontology-6#"+medicationNameArr.get(i+1));
					StmtIterator iterator = model.listStatements();
					while(iterator.hasNext())
					{
						Statement s=iterator.nextStatement();
						 if(s.getPredicate().equals(property1) )
							 {
							 if(s.getSubject().equals(med1) || s.getSubject().equals(med2))
						      {
								 if(s.getObject().equals(med2) || s.getObject().equals(med1))
							      {
								System.out.println("Severity level : Major");
								break;
						      }
							 }
							 }
						 else if(s.getPredicate().equals(property2) )
							 {
							 if( s.getSubject().equals(med1) || s.getSubject().equals(med2))
							{
								 if(s.getObject().equals(med2) || s.getObject().equals(med1))
							      {
								System.out.println("Severity level : Minor");
								break;
							}
							}
							 }
						 else if(s.getPredicate().equals(property3))
						 {
							 if( s.getSubject().equals(med1) || s.getSubject().equals(med2))
							{
								 if(s.getObject().equals(med2) || s.getObject().equals(med1))
							      {
								System.out.println("Severity level : Moderate");
								break;
							}
						 }
						 }
					}
				}
			}
			else
			{
				if ((startDateArr.get(0).compareTo(endDateArr.get(medicationNameArr.size()-1))<=0) && 
					(startDateArr.get(medicationNameArr.size()-1).compareTo(endDateArr.get(0))<=0))
				{
					System.out.println("ALERT!! " + medicationNameArr.get(0) +" "+ medicationNameArr.get(medicationNameArr.size()-1));
					Resource med1 = model.getResource("http://www.semanticweb.org/dell/ontologies/2021/11/untitled-ontology-6#"+medicationNameArr.get(0));
					Resource med2 = model.getResource("http://www.semanticweb.org/dell/ontologies/2021/11/untitled-ontology-6#"+medicationNameArr.get(medicationNameArr.size()-1));
					StmtIterator iterator = model.listStatements();
					while(iterator.hasNext())
					{
						Statement s=iterator.nextStatement();
						 if(s.getPredicate().equals(property1) )
							 {
							 if(s.getSubject().equals(med1) || s.getSubject().equals(med2))
						      {
								 if(s.getObject().equals(med2) || s.getObject().equals(med1))
							      {
								System.out.println("Severity level : Major");
								break;
						      }
							 }
							 }
						 else if(s.getPredicate().equals(property2) )
							 {
							 if( s.getSubject().equals(med1) || s.getSubject().equals(med2))
							{
								 if(s.getObject().equals(med2) || s.getObject().equals(med1))
							      {
								System.out.println("Severity level : Minor");
								break;
							}
							}
							 }
						 else if(s.getPredicate().equals(property3))
						 {
							 if( s.getSubject().equals(med1) || s.getSubject().equals(med2))
							{
								 if(s.getObject().equals(med2) || s.getObject().equals(med1))
							      {
								System.out.println("Severity level : Moderate");
								break;
							}
						 }
						 }
					}
					}			
			}
		}
	}
}
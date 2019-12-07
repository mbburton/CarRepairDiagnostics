package com.ubiquisoft.evaluation;

import com.ubiquisoft.evaluation.domain.Car;
import com.ubiquisoft.evaluation.domain.ConditionType;
import com.ubiquisoft.evaluation.domain.Part;
import com.ubiquisoft.evaluation.domain.PartType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;

public class CarDiagnosticEngine {

	public void executeDiagnostics(Car car) {
		/*
		 * Implement basic diagnostics and print results to console.
		 *
		 * The purpose of this method is to find any problems with a car's data or parts.
		 *
		 * Diagnostic Steps:
		 *      First   - Validate the 3 data fields are present, if one or more are
		 *                then print the missing fields to the console
		 *                in a similar manner to how the provided methods do.
		 *
		 *      Second  - Validate that no parts are missing using the 'getMissingPartsMap' method in the Car class,
		 *                if one or more are then run each missing part and its count through the provided missing part method.
		 *
		 *      Third   - Validate that all parts are in working condition, if any are not
		 *                then run each non-working part through the provided damaged part method.
		 *
		 *      Fourth  - If validation succeeds for the previous steps then print something to the console informing the user as such.
		 * A damaged part is one that has any condition other than NEW, GOOD, or WORN.
		 *
		 * Important:
		 *      If any validation fails, complete whatever step you are actively one and end diagnostics early.
		 *
		 * Treat the console as information being read by a user of this application. Attempts should be made to ensure
		 * console output is as least as informative as the provided methods.
		 */

		//Missing Data Fields - Step 1
		List<String> missingDataFields = getMissingDataFields(car);

		if(!missingDataFields.isEmpty()){
			for(String missingDataField : missingDataFields){
				printMissingDataField(missingDataField);
			}

			printDiagnosticsFailAndExit("There were missing data fields.");
		}

		//Missing Parts - Step 2
		Map<PartType, Integer> missingPartsMap = car.getMissingPartsMap();

		if(!missingPartsMap.isEmpty()){
			for(Map.Entry<PartType, Integer> entry : missingPartsMap.entrySet()){
				printMissingPart(entry.getKey(), entry.getValue());
			}

			printDiagnosticsFailAndExit("There were missing parts.");

		}

		//Damaged Parts - Step 3
		Map<PartType, ConditionType> damagedParts = findDamagedParts(car);

		if(!damagedParts.isEmpty()){
			for(Map.Entry<PartType, ConditionType> entry : damagedParts.entrySet()){
				printDamagedPart(entry.getKey(), entry.getValue());
			}

			printDiagnosticsFailAndExit("There were damaged parts.");
		}

		//Diagnostics Success - Step 4
		printDiagnosticsSuccessAndExit(car);
	}

	private void printDiagnosticsSuccessAndExit(Car car){
		System.out.println(String.format("The car Year: %s, Make: %s, and Model: %s has passed diagnostics.", car.getYear(), car.getMake(), car.getModel()));
		System.exit(1);
	}

	private Map<PartType, ConditionType> findDamagedParts(Car car){
		Map<PartType, ConditionType> damagedParts = new HashMap<>();
		for(Part p: car.getParts()){
			if(!p.isInWorkingCondition()){
				damagedParts.put(p.getType(), p.getCondition());
			}
		}
		return damagedParts;
	}

	private List<String> getMissingDataFields(Car car){
		List<String> missingDataFields = new ArrayList<>();
		if(car.getMake() == null || car.getMake().isEmpty()){
			missingDataFields.add("make");
		}

		if(car.getModel() == null || car.getModel().isEmpty()){
			missingDataFields.add("model");
		}

		if(car.getYear() == null || car.getYear().isEmpty()){
			missingDataFields.add("year");
		}

		return missingDataFields;
	}

	private void printDiagnosticsFailAndExit(String reason){
		System.out.println(String.format("The diagnostics have failed for the following reason: %s", reason));
		System.exit(2);
	}

	private void printMissingDataField(String missingField) {
		if(missingField == null)  throw new IllegalArgumentException("Missing Field must not be null");
		System.out.println(String.format("The car is missing the following data field: %s", missingField));
	}

	private void printMissingPart(PartType partType, Integer count) {
		if (partType == null) throw new IllegalArgumentException("PartType must not be null");
		if (count == null || count <= 0) throw new IllegalArgumentException("Count must be greater than 0");

		System.out.println(String.format("Missing Part(s) Detected: %s - Count: %s", partType, count));
	}

	private void printDamagedPart(PartType partType, ConditionType condition) {
		if (partType == null) throw new IllegalArgumentException("PartType must not be null");
		if (condition == null) throw new IllegalArgumentException("ConditionType must not be null");

		System.out.println(String.format("Damaged Part Detected: %s - Condition: %s", partType, condition));
	}

	public static void main(String[] args) throws JAXBException {
		// Load classpath resource
		InputStream xml = ClassLoader.getSystemResourceAsStream("SampleCar.xml");

		// Verify resource was loaded properly
		if (xml == null) {
			System.err.println("An error occurred attempting to load SampleCar.xml");

			System.exit(1);
		}

		// Build JAXBContext for converting XML into an Object
		JAXBContext context = JAXBContext.newInstance(Car.class, Part.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		Car car = (Car) unmarshaller.unmarshal(xml);

		// Build new Diagnostics Engine and execute on deserialized car object.

		CarDiagnosticEngine diagnosticEngine = new CarDiagnosticEngine();

		diagnosticEngine.executeDiagnostics(car);

	}

}

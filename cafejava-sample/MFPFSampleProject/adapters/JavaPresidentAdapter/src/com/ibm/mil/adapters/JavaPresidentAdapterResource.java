/*
 *    Licensed Materials - Property of IBM
 *    5725-I43 (C) Copyright IBM Corp. 2015. All Rights Reserved.
 *    US Government Users Restricted Rights - Use, duplication or
 *    disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package com.ibm.mil.adapters;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.worklight.adapters.rest.api.WLServerAPI;
import com.worklight.adapters.rest.api.WLServerAPIProvider;

import com.ibm.mil.database.SampleDatabase;
import com.ibm.mil.database.models.President;


@Path("/presidents")
public class JavaPresidentAdapterResource {
	/**
	 * For more info on JAX-RS see
	 * https://jsr311.java.net/nonav/releases/1.1/index.html
	 */
	public JavaPresidentAdapterResource() {
		
	}
	
	// Define logger (Standard java.util.Logger)
	static Logger logger = Logger.getLogger(JavaPresidentAdapterResource.class
			.getName());

	// Define the server api to be able to perform server operations
	WLServerAPI api = WLServerAPIProvider.getWLServerAPI();

	/*
	 * Path for method:
	 * "<server address>/MFPSFampleProject/adapters/JavaPresidentAdapter/presidents/{president_number}"
	 */
	@GET
	@Path("/{president_number}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("president_number") int presidentNumber) {
		President returnedUser = SampleDatabase.getSingleton().getPresident(presidentNumber);

		if (returnedUser == null) {
			return Response.serverError()
					.entity("GET FAILED: Could not find a president matching given presidentNumber " + presidentNumber)
					.build();
		}

		return Response.ok().entity(returnedUser).build();

	}

	/*
	 * Path for method:
	 * "<server address>/MFPSFampleProject/adapters/JavaPresidentAdapter/presidents/{president_number}"
	 */
	@DELETE
	@Path("/{president_number}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePresident(@PathParam("president_number") int presidentNumber) {
		President deletedPresident = SampleDatabase.getSingleton().deleteUser(presidentNumber);
		
		if (deletedPresident == null) {
			return Response.serverError()
					.entity("DELETE FAILED: Could not find a president matching given username")
					.build();
		}
		
		return Response.ok().entity(deletedPresident).build();
	}
	
	/*
	 * Path for method:
	 * "<server address>/MFPSFampleProject/adapters/JavaPresidentAdapter/presidents/{president_number}"
	 */
	@PUT
	@Path("/{president_number}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePresident(@PathParam("president_number") int presidentNumber, President newPresident) {
		President updatedPresident = SampleDatabase.getSingleton().updateUser(presidentNumber, newPresident);
		
		if (updatedPresident == null) {
			return Response.serverError()
					.entity("PUT FAILED: Error updating previous president " + presidentNumber)
					.build();
		}
		
		return Response.ok().entity(updatedPresident).build();
	}
	
	/*
	 * Path for method:
	 * "<server address>/MFPSFampleProject/adapters/JavaPresidentAdapter/presidents/"
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPresident(President newPresident) {
		President createdPresident = SampleDatabase.getSingleton().createUser(newPresident);
		
		if (createdPresident == null) {
			return Response.serverError()
					.entity("POST FAILED: Error creating a new president")
					.build();
		}
		
		return Response.ok().entity(createdPresident).build();
	}


	public static void main(String[] args) {
		JavaPresidentAdapterResource test = new JavaPresidentAdapterResource();
		System.out.println(test.getUser(1));
	}
}

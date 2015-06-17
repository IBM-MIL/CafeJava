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

import com.ibm.mil.database.SampleDatabase;
import com.ibm.mil.database.models.User;
import com.worklight.adapters.rest.api.WLServerAPI;
import com.worklight.adapters.rest.api.WLServerAPIProvider;

@Path("/users")
public class JavaSampleAdapterResource {
	/*
	 * For more info on JAX-RS see
	 * https://jsr311.java.net/nonav/releases/1.1/index.html
	 */
	public JavaSampleAdapterResource() {
		
	}
	
	// Define logger (Standard java.util.Logger)
	static Logger logger = Logger.getLogger(JavaSampleAdapterResource.class
			.getName());

	// Define the server api to be able to perform server operations
	WLServerAPI api = WLServerAPIProvider.getWLServerAPI();

	/*
	 * Path for method:
	 * "<server address>/MFPSampleProject/adapters/JavaSampleAdapter/users/{username}"
	 */
	@GET
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("username") String username) {
		User returnedUser = SampleDatabase.getSingleton().getUser(username);

		if (returnedUser == null) {
			return Response.serverError()
					.entity("GET FAILED: Could not find user matching given username")
					.build();
		}

		return Response.ok().entity(returnedUser).build();

	}

	/*
	 * Path for method:
	 * "<server address>/MFPSampleProject/adapters/JavaSampleAdapter/users/{username}"
	 */
	@DELETE
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUser(@PathParam("username") String username) {
		User deletedUser = SampleDatabase.getSingleton().deleteUser(username);
		
		if (deletedUser == null) {
			return Response.serverError()
					.entity("DELETE FAILED: Could not find user matching given username")
					.build();
		}
		
		return Response.ok().entity(deletedUser).build();
	}
	
	/*
	 * Path for method:
	 * "<server address>/MFPSampleProject/adapters/JavaSampleAdapter/users/{username}"
	 */
	@PUT
	@Path("/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(@PathParam("username") String username, User newUser) {
		User updatedUser = SampleDatabase.getSingleton().updateUser(username, newUser);
		
		if (updatedUser == null) {
			return Response.serverError()
					.entity("PUT FAILED: Error updating previous user" + username)
					.build();
		}
		
		return Response.ok().entity(updatedUser).build();
	}
	
	/*
	 * Path for method:
	 * "<server address>/MFPSampleProject/adapters/JavaSampleAdapter/users/"
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUser(User newUser) {
		User createdUser = SampleDatabase.getSingleton().createUser(newUser);
		
		if (createdUser == null) {
			return Response.serverError()
					.entity("POST FAILED: Error creating new user")
					.build();
		}
		
		return Response.ok().entity(createdUser).build();
	}


	public static void main(String[] args) {
		JavaSampleAdapterResource test = new JavaSampleAdapterResource();
		System.out.println(test.getUser("bobcat"));
	}
}

/*
 *  Licensed Materials - Property of IBM
 *  5725-I43 (C) Copyright IBM Corp. 2011, 2013. All Rights Reserved.
 *  US Government Users Restricted Rights - Use, duplication or
 *  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */


/**
 * getPersonFlat() returns a flat Person object.
 *
 * @returns a person object in a flat response
 * 		response is formatted :
 * 			{
 *              "isDeveloper": true,
 *              "isSuccessful": true,
 *              "age": 22,
 *              "name": "FirstName LastName"
 *          }
 *
 */
function getPersonFlat() {

	var person = {};
	person.name = 'Johnny Appleseed';
	person.age = 22;
	person.isDeveloper = true;

	return person;
}



/**
 * getPersonNested() returns a Person object that is nested inside of the 'person' field.
 *
 * @returns a person object within the 'person' field
 * 		response is formatted :
 *			{
 *			  "person": {
 *				"isDeveloper": true,
 *				"age": 22,
 * 				"name": "FirstName LastName"
 *			  },
 *			  "isSuccessful": true
 *			}
 *
 */
function getPersonNested() {
	var result = {};

	var person = {};
	person.name = 'James Monroe';
	person.age = 22;
	person.isDeveloper = true;

	result.person = person;

	return result;

}


/**
 * getAllPersonsFlat() an array of all the Person objects.
 *
 * @returns an array of Person objects.
 * 		example response is formatted :
 *		   {
 *			 "isSuccessful": true,
 *			 "persons": [
 *			   {
 *				 "isDeveloper": true,
 *				 "age": 20,
 *				 "name": "Name0"
 *			   },
 *			   {
 *				 "isDeveloper": true,
 *				 "age": 21,
 *				 "name": "Name1"
 *			   },
 *			   {
 * 				 "isDeveloper": true,
 *				 "age": 22,
 *				 "name": "Name2"
 *			   },
 *			   {
 *				 "isDeveloper": true,
 *				 "age": 23,
 *				 "name": "Name3"
 *			   }
 *			 ]
 *		   }
 *
 */
function getAllPersonsFlat() {
	var TOTAL_PERSONS = 4;
	var START_AGE = 20;

	var result = {};

	allPersons = [];

	for (i = 0; i < TOTAL_PERSONS; i++) {
		var person = {};
		person.name = 'FlatName' + i;
		person.age = START_AGE + i;
		person.isDeveloper = true;

		allPersons[i] = person;
	}

	result.persons = allPersons;

	return result;
}

/**
 * getAllPersonsNested() an array of all the Person objects nested in a 'response' field.
 *
 * @returns an array of Person objects.
 * 		example response is formatted :
 *		   {
 *			 "isSuccessful": true,
 *			 "reponse": {
 *			 	"persons": [
 *			   	{
 *					"isDeveloper": true,
 *				 	"age": 20,
 *				 	"name": "Name0"
 *			   	},
 *			   	{
 *				 	"isDeveloper": true,
 *				 	"age": 21,
 *				 	"name": "Name1"
 *			   	},
 *			   	{
 * 				 	"isDeveloper": true,
 *				 	"age": 22,
 *				 	"name": "Name2"
 *			   	},
 *			   	{
 *				 	"isDeveloper": true,
 *				 	"age": 23,
 *				 	"name": "Name3"
 *			   	}
 *			 	]
 *              "size":4
 *			}
 *		  }
 *
 */
function getAllPersonsNested() {
	var TOTAL_PERSONS = 4;
	var START_AGE = 20;

	var result = {};
	result.response = {};

	allPersons = [];

	for (i = 0; i < TOTAL_PERSONS; i++) {
		var person = {};
		person.name = 'NestedName' + i;
		person.age = START_AGE + i;
		person.isDeveloper = true;

		allPersons[i] = person;
	}

	result.response.persons = allPersons;
	result.response.size = TOTAL_PERSONS;

	return result;
}

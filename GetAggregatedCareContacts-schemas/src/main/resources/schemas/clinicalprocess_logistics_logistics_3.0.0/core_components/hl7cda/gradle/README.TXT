====
    Copyright (c) 2014 Inera AB, <http://inera.se/>

    This file is part of SKLTP.

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
====

The purpose of this directory is to verify the transformation between CDA and GreenCDA
Prerequisites are
	Java SDK
	Gradle

Once this is installed one can run the following from the commandline

	$ gradle test

This will run through the steps explained below.

	1.	Take the .xml files from ../schemas/interactions/GetCareDocumentationInteraction/hl7cda/samples and apply the ../schemas/interactions/GetCareDocumentationInteraction/hl7cda/transformations/CDA_to_GreenCDA.xslt
		and write the result of the transformation to ./test/GreenCDA/

	2.	Take the .xml files from ./test/GreenCDA/ and apply the ./schemas/interactions/GetCareDocumentationInteraction/hl7cda/transformations/GreenCDA_to_CDA.xslt
		and write the result of the transformation to ./test/CDA/

	3.	Run XMLDiff on all .xml files in ./test/CDA/ and compare them to the original .xml files in ../schemas/interactions/GetCareDocumentationInteraction/hl7cda/samples

	4.	Run xml schema validation on all .xml files in ./test/GreenCDA

	5.	Run xml schema validation on all .xml files in ./test/CDA

# eap-additional-testsuite-source-distribution
==============================================

The code to produce the jar to be used in order to distribute the test files to the equivalent subset of the tested software. 

Receives as inputs :
-------------------
1. args[0] : the ${basedir} of eap-additional-testsuite
2. args[1] : the source directory of eap-additional-testsuite : ${basedir}/modules/src/main/java
3. args[2] : the directory of the test subset
4. args[3] : the version of the sofware being tested
5. args[4] : the directory where the version order map exists (optional)
6. args[5] : true/false value to disable all the tests in the testsuite (optional)
7. args[6] : the path to the feature list, which is used with the @ATFeature annotation (optional)
8. args[7] : the path to the feature list, which is used with the @EATDMP annotation (optional)
9. args[8] : true/false value to disable all the tests in the testsuite in case the version included is a SNAPSHOT (optional)

Purpose :
---------
The code searches the specified source directory for java files with the class annotation @EapAdditionalTestsuite and distributes the java files to the equivalent directories (which are specified as an attribute of @EapAdditionalTestsuite).

@EapAdditionalTestsuite
-----------------------
Class level annotation used for distribution of the sources to the equivalent Server directory of eap-additional-testsuite.
Its attribute is an array of Strings that specifies the destination directories of the sources.

@ATTEST
-------
Method level annotation that enables/disables specific tests according to the version of the software being tested.

@ATFeature
----------
Method level annotation that enables test method distribution if the pre-specified features are included in feature list of the tested software.

@EATDMP
-------
Class and method level annotaion that enables the distribution of classes/methods, if the required features are included in the tested software. Can be used for dynamic server creation testing.

#License 
* [GNU Lesser General Public License Version 2.1](http://www.gnu.org/licenses/lgpl-2.1-standalone.html)

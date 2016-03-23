# eap-additional-testsuite-source-distribution

The code to produce the jar to be used in order to distribute the test files to the equivalent servers. To be used along with the eap-additional-testsuite.

Receives as inputs :
1. args[0] : the ${basedir} of eap-additional-testsuite
2. args[1] : the source directory of eap-additional-testsuite : ${basedir}/modules/src/main/java

The code searches the specified source directory for java files with the class annotation @EapAdditionalTestsuite and distributes the java files to the equivalent server directories (which are specified as an attribute of @EapAdditionalTestsuite), in order to be processed as eap-additional-testsuite Server TestCases.

#License 
* [GNU Lesser General Public License Version 2.1](http://www.gnu.org/licenses/lgpl-2.1-standalone.html)

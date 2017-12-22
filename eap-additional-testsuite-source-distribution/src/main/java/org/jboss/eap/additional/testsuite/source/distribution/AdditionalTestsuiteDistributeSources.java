/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.eap.additional.testsuite.source.distribution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author panos
 */
public class AdditionalTestsuiteDistributeSources {

    /**
     * @param args the command line arguments
     * args[0] : the ${basedir} of eap-additional-testsuite
     * args[1] : the source directory of eap-additional-testsuite : ${basedir}/modules/src/main/java
     * args[2] : the server to distribute the sources to
     */
    public static void main(String[] args) throws IOException {
        String featureListFile = args[6];
        FeatureData featureDataList = new FeatureData();
        if (featureListFile != null) {
            File featureList = new File(featureListFile);
            
            if (featureList.exists()) {
                try {
                    BufferedReader in = new BufferedReader(
                            new FileReader(featureListFile));
                    String str;

                    while ((str = in.readLine()) != null) {
                        String[] ar = str.split(",");

                        if (ar.length == 1) {
                            featureDataList.feature.add(ar[0].trim());
                            featureDataList.minVersion.add(null);
                            featureDataList.maxVersion.add(null);
                            featureDataList.resource.add(null);
                            featureDataList.params.add(null);
                        } else if (ar.length == 2) {
                            featureDataList.feature.add(ar[0].trim());
                            featureDataList.minVersion.add(ar[1].trim());
                            featureDataList.maxVersion.add(null);
                            featureDataList.resource.add(null);
                            featureDataList.params.add(null);
                        } else if (ar.length == 3) {
                            featureDataList.feature.add(ar[0].trim());
                            featureDataList.minVersion.add(ar[1].trim());
                            featureDataList.maxVersion.add(ar[2].trim());
                            featureDataList.resource.add(null);
                            featureDataList.params.add(null);
                        } else if (ar.length == 4) {
                            featureDataList.feature.add(ar[0].trim());
                            featureDataList.minVersion.add(ar[1].trim());
                            featureDataList.maxVersion.add(ar[2].trim());
                            featureDataList.resource.add(ar[3].trim());
                            featureDataList.params.add(null);
                        } else if (ar.length > 4) {
                            featureDataList.feature.add(ar[0].trim());
                            featureDataList.minVersion.add(ar[1].trim());
                            featureDataList.maxVersion.add(ar[2].trim());
                            featureDataList.resource.add(ar[3].trim());
                            String parameters = "";
                            for (int i = 4; i < ar.length; i++) {
                                parameters = parameters + ar[i].trim() + ",";
                            }
                            featureDataList.params.add(parameters);
                        }
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        ProcessSources.AdditionalTestSuiteAnnotationProcessing(args[0],args[1],args[2],args[3],args[4],Boolean.parseBoolean(args[5]),featureDataList);
    //    ProcessSources.AdditionalTestSuiteAnnotationProcessing("/home/panos/RC/eap-additional-testsuite3","/home/panos/RC/eap-additional-testsuite3/modules/src/main/java","Wildfly","12.0.0.Alpha1-SNAPSHOT","vesrionOrder",Boolean.parseBoolean(null),featureDataList);
    }
    
}

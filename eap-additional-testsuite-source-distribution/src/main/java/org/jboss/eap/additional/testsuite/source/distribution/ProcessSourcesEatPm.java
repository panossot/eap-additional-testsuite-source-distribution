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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author panos
 */
public class ProcessSourcesEatPm {
    
    static String versionOrder = "Alpha1,Alpha2,Beat1,Beta2,CR1,CR2,CR3,GA,Final";

    public static void EatPmAnnotationProcessing(String basedir, String sourcePath, HashMap<String,PMFeatureData> pmFeatureDataList, ArrayList<String> excludedFiles) {
        File folder = new File(sourcePath);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return;
        }

        try {
            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    EatPmAnnotationProcessing(basedir, file.getAbsolutePath(), pmFeatureDataList, excludedFiles);
                } else {
                    checkFileForAnnotation(file.getAbsolutePath(), "@EATDPM", pmFeatureDataList, excludedFiles);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static boolean checkFileForAnnotation(String file, String annotationName, HashMap<String,PMFeatureData> pmFeatureDataList, ArrayList<String> excludedFiles) throws ClassNotFoundException {
        String annotationLine = null;
        boolean result = true;
        File f = new File(file);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            int lineNum=0;
            while ((line = reader.readLine()) != null && !excludedFiles.contains(file)) {
                if (line.contains(annotationName)) {
                    annotationLine = line;
                    String isClassAnnotation = annotationLine.substring(annotationLine.lastIndexOf("isClassAnnotation=\"") + 19, annotationLine.lastIndexOf("isClassAnnotation=\"") + 19 + annotationLine.substring(annotationLine.lastIndexOf("isClassAnnotation=\"") + 19).indexOf("\""));
                    
                    if(isClassAnnotation.contains("true")) {
                        String config = annotationLine.substring(annotationLine.lastIndexOf("config=\"") + 8, annotationLine.lastIndexOf("config=\"") + 8 + annotationLine.substring(annotationLine.lastIndexOf("config=\"") + 8).indexOf("\""));

                        if (pmFeatureDataList.containsKey(config)) {
                            String[] features = annotationLine.substring(annotationLine.lastIndexOf("features={\"") + 11, annotationLine.lastIndexOf("features={\"") + 11 + annotationLine.substring(annotationLine.lastIndexOf("features={\"") + 11).indexOf("\"}")).split(",");
                            
                            String[] minVersions = null;
                            if (annotationLine.lastIndexOf("minVersions={\"") != -1) {
                                minVersions = annotationLine.substring(annotationLine.lastIndexOf("minVersions={\"") + 14, annotationLine.lastIndexOf("minVersions={\"") + 14 + annotationLine.substring(annotationLine.lastIndexOf("minVersions={\"") + 14).indexOf("\"}")).split(",");
                            }
                            
                            String[] maxVersions = null;
                            if (annotationLine.lastIndexOf("maxVersions={\"") != -1) {
                                maxVersions = annotationLine.substring(annotationLine.lastIndexOf("maxVersions={\"") + 14, annotationLine.lastIndexOf("maxVersions={\"") + 14 + annotationLine.substring(annotationLine.lastIndexOf("maxVersions={\"") + 14).indexOf("\"}")).split(",");
                            }
                            
                            int index = 0;
                            for(String feature : features) {
                                if(pmFeatureDataList.get(config).feature.contains(feature)) {
                                    String version = pmFeatureDataList.get(config).version.get(pmFeatureDataList.get(config).feature.indexOf(feature));
                                    String minVersion = minVersions[index];
                                    String maxVersion = maxVersions[index];
                                    
                                    if (minVersion != null) {
                                        String[] versionRelease = version.split("-");
                                        int verRelease1 = 0;
                                        String[] verPart = versionRelease[0].split("\\.");
                                        if (verPart.length > 2) {
                                            verRelease1 = Integer.parseInt(verPart[0] + verPart[1] + verPart[2]);
                                        }
                                        String[] subVersions = minVersion.split("-");
                                        String[] verPart2 = subVersions[0].split("\\.");
                                        int verRelease2 = 0;                                                                   
                                        if (verPart2.length > 2) {
                                            verRelease2 = Integer.parseInt(verPart2[0] + verPart2[1] + verPart2[2]);
                                        }
                                        
                                        if (verRelease1 == verRelease2 && verPart.length > 3 && verPart2.length > 3){
                                            if (verPart[3]!=null && verPart2[3]!=null && versionOrder.indexOf(verPart[3])!=-1 && versionOrder.indexOf(verPart2[3])!=-1) {
                                                if(versionOrder.indexOf(verPart[3]) > versionOrder.indexOf(verPart2[3]))
                                                    verRelease2--;
                                                else if(versionOrder.indexOf(verPart[3]) < versionOrder.indexOf(verPart2[3]))
                                                    verRelease2++;
                                            }
                                        }

                                        int verRelease3 = Integer.MAX_VALUE;

                                        if (maxVersion != null) {
                                            String[] subVersionsMax = maxVersion.split("-");
                                            verPart2 = subVersionsMax[0].split("\\.");

                                            if (verPart2.length > 2) {
                                                verRelease3 = Integer.parseInt(verPart2[0] + verPart2[1] + verPart2[2]);
                                            }
                                            
                                            if (verRelease1 == verRelease3 && verPart.length > 3 && verPart2.length > 3){
                                            if (verPart[3]!=null && verPart2[3]!=null && versionOrder.indexOf(verPart[3])!=-1 && versionOrder.indexOf(verPart2[3])!=-1) {
                                                if(versionOrder.indexOf(verPart[3]) > versionOrder.indexOf(verPart2[3]))
                                                    verRelease3--;
                                                else if(versionOrder.indexOf(verPart[3]) < versionOrder.indexOf(verPart2[3]))
                                                    verRelease3++;
                                            }
                                        }
                                        }
                                        
                                        if((verRelease1 >= verRelease2) && (verRelease1 <= verRelease3)) {
                                            result = true;
                                        }else {
                                            result = false;
                                            break;
                                        }
                                    }else
                                        result = true;
                                    
                                    
                                }else {
                                    result = false;
                                    break;
                                }
                                
                                index++;
                            }

                        }
                        
                        if (result)
                           break;
                        
                    }else {
                        boolean methodResult = true;
                        String config = annotationLine.substring(annotationLine.lastIndexOf("config=\"") + 8, annotationLine.lastIndexOf("config=\"") + 8 + annotationLine.substring(annotationLine.lastIndexOf("config=\"") + 8).indexOf("\""));
                        
                        if (pmFeatureDataList.containsKey(config)) {
                            String[] features = annotationLine.substring(annotationLine.lastIndexOf("features={\"") + 11, annotationLine.lastIndexOf("features={\"") + 11 + annotationLine.substring(annotationLine.lastIndexOf("features={\"") + 11).indexOf("\"}")).split(",");
                            
                            String[] minVersions = null;
                            if (annotationLine.lastIndexOf("minVersions={\"") != -1) {
                                minVersions = annotationLine.substring(annotationLine.lastIndexOf("minVersions={\"") + 14, annotationLine.lastIndexOf("minVersions={\"") + 14 + annotationLine.substring(annotationLine.lastIndexOf("minVersions={\"") + 14).indexOf("\"}")).split(",");
                            }
                            
                            String[] maxVersions = null;
                            if (annotationLine.lastIndexOf("maxVersions={\"") != -1) {
                                maxVersions = annotationLine.substring(annotationLine.lastIndexOf("maxVersions={\"") + 14, annotationLine.lastIndexOf("maxVersions={\"") + 14 + annotationLine.substring(annotationLine.lastIndexOf("maxVersions={\"") + 14).indexOf("\"}")).split(",");
                            }
                            
                            int index = 0;
                            for(String feature : features) {
                                if(pmFeatureDataList.get(config).feature.contains(feature)) {
                                    String version = pmFeatureDataList.get(config).version.get(pmFeatureDataList.get(config).feature.indexOf(feature));
                                    String minVersion = minVersions[index];
                                    String maxVersion = maxVersions[index];
                                    
                                    if (minVersion != null) {
                                        String[] versionRelease = version.split("-");
                                        int verRelease1 = 0;
                                        String[] verPart = versionRelease[0].split("\\.");
                                        if (verPart.length > 2) {
                                            verRelease1 = Integer.parseInt(verPart[0] + verPart[1] + verPart[2]);
                                        }
                                        String[] subVersions = minVersion.split("-");
                                        String[] verPart2 = subVersions[0].split("\\.");
                                        int verRelease2 = 0;                                                                   
                                        if (verPart2.length > 2) {
                                            verRelease2 = Integer.parseInt(verPart2[0] + verPart2[1] + verPart2[2]);
                                        }
                                        
                                        if (verRelease1 == verRelease2 && verPart.length > 3 && verPart2.length > 3){
                                            if (verPart[3]!=null && verPart2[3]!=null && versionOrder.indexOf(verPart[3])!=-1 && versionOrder.indexOf(verPart2[3])!=-1) {
                                                if(versionOrder.indexOf(verPart[3]) > versionOrder.indexOf(verPart2[3]))
                                                    verRelease2--;
                                                else if(versionOrder.indexOf(verPart[3]) < versionOrder.indexOf(verPart2[3]))
                                                    verRelease2++;
                                            }
                                        }

                                        int verRelease3 = Integer.MAX_VALUE;

                                        if (maxVersion != null) {
                                            String[] subVersionsMax = maxVersion.split("-");
                                            verPart2 = subVersionsMax[0].split("\\.");

                                            if (verPart2.length > 2) {
                                                verRelease3 = Integer.parseInt(verPart2[0] + verPart2[1] + verPart2[2]);
                                            }
                                            
                                            if (verRelease1 == verRelease3 && verPart.length > 3 && verPart2.length > 3){
                                                if (verPart[3]!=null && verPart2[3]!=null && versionOrder.indexOf(verPart[3])!=-1 && versionOrder.indexOf(verPart2[3])!=-1) {
                                                    if(versionOrder.indexOf(verPart[3]) > versionOrder.indexOf(verPart2[3]))
                                                        verRelease3--;
                                                    else if(versionOrder.indexOf(verPart[3]) < versionOrder.indexOf(verPart2[3]))
                                                        verRelease3++;
                                                }
                                            }
                                        }
                                        
                                        if((verRelease1 >= verRelease2) && (verRelease1 <= verRelease3)) {
                                            methodResult = true;
                                        }else {
                                            methodResult = false;
                                            break;
                                        }
                                    }else
                                        methodResult = true;
                                    
                                    
                                }else {
                                    methodResult = false;
                                    break;
                                }
                                
                                index++;
                            }

                        }
                        
                        if(!methodResult) {
                            reader = deleteMethodEATDPM(lineNum,file);
                            
                            for(int i=0; i<lineNum; i++){
                                reader.readLine();
                            }
                        }
                    }

                }
                lineNum++;
            }
            
            if(!result)
                excludedFiles.add(file);
                
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return result;
    }
    
    private static BufferedReader deleteMethodEATDPM(int lineNum, String file) throws IOException{
        List<String> lines = FileUtils.readLines(new File(file), "utf-8");
        
        while(lines.get(lineNum).compareTo("")!=0){
            lineNum--;
        }
        if(lines.get(lineNum).compareTo("")==0) {
            int up=0;
            int down=0;
            lineNum++;
            
            while ((lines!=null && lines.size()>lineNum-1 && lines.get(lineNum)!=null && lines.get(lineNum).compareTo("")!=0) || up==0 || up!=down) {
                System.out.println(lineNum + " " + lines.get(lineNum).compareTo("") + " " + (up!=down));
                String line = lines.get(lineNum);
                while(line.contains("{")) {
                    line = line.replaceFirst("\\{", "");
                    up++;
                }
                while(line.contains("}")) {
                    line = line.replaceFirst("\\}", "");
                    down++;
                }
                lines.remove(lineNum);
            }
            
            FileWriter writer = new FileWriter(file); 
            for(String str: lines) {
              writer.write(str + "\n");
            }
            writer.close();
            
        }
        
        return (new BufferedReader(new FileReader(file)));
    }

}


class PMFeatureData {

    protected String config;
    protected ArrayList<String> feature;
    protected ArrayList<String> version;
    protected int linenum;

    public PMFeatureData() {
        feature = new ArrayList();
        version = new ArrayList();
    }

    public PMFeatureData(String config, ArrayList<String> feature, ArrayList<String> version) {
        this.config = config;
        this.feature = feature;
        this.version = version;
    }
    
    public PMFeatureData(ArrayList<String> feature, ArrayList<String> version) {
        this.feature = feature;
        this.version = version;
    }

    public PMFeatureData(ArrayList<String> feature, ArrayList<String> version, int lineNum) {
        this.feature = feature;
        this.version = version;
        this.linenum = lineNum;
    }

}

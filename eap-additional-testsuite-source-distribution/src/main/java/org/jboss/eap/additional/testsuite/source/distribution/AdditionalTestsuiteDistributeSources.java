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
    public static void main(String[] args) {
        ProcessSources.AdditionalTestSuiteAnnotationProcessing(args[0],args[1],args[2]);
    }
    
}

/***********************************************************************************************
 *
 * Copyright (C) 2016, IBL Software Engineering spol. s r. o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * ---------------------------------------------------------------------------------------------
 *
 * Notice:
 *
 * This file is based on source code from Command-line Refuter of Unshapely XML (CRUX)
 * developed by Developed by NCAR's Research Applications Laboratory (http://www.ral.ucar.edu)
 *
 * Copyright (c) 2016. University Corporation for Atmospheric Research (UCAR). All rights reserved.
 * See also https://github.com/NCAR/crux/blob/master/LICENSE for more information about CRUX license.
 *
 ***********************************************************************************************/

package com.iblsoft.iwxxm.webservice.validator;

import com.iblsoft.iwxxm.webservice.util.Log;
import org.apache.xerces.impl.xs.XSDDescription;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;

import java.io.IOException;

/**
 * Extends Xerces's XMLCatalogResolver for debugging purposes.  By default debugging messages are disabled
 */
class XmlCatalogResolver extends org.apache.xerces.util.XMLCatalogResolver {
    private boolean allowRemoteResources;

    XmlCatalogResolver(String[] catalogLocations, boolean preferPublic) {
        this(catalogLocations, preferPublic, false);
    }

    /**
     * @param catalogLocations the path to XML catalog files
     * @param preferPublic     whether public or system matches are preferred
     */
    XmlCatalogResolver(String[] catalogLocations, boolean preferPublic, boolean allowRemoteResources) {
        this.allowRemoteResources = allowRemoteResources;
        setCatalogList(catalogLocations);
        setPreferPublic(preferPublic);
    }


    @Override
    public String resolveIdentifier(XMLResourceIdentifier xmlResourceIdentifier) throws IOException, XNIException {
        if (xmlResourceIdentifier == null || !(xmlResourceIdentifier instanceof XSDDescription) ||
                xmlResourceIdentifier.getNamespace() == null) {
            return super.resolveIdentifier(xmlResourceIdentifier);
        }
        XSDDescription desc = (XSDDescription) xmlResourceIdentifier;
        String id = super.resolveIdentifier(xmlResourceIdentifier);

        String expandedSystemId = id;
        if (expandedSystemId == null) {
            expandedSystemId = xmlResourceIdentifier.getExpandedSystemId();
        }

        if (!expandedSystemId.startsWith("file:")) {
            Log.INSTANCE.warn("Identifier {} is not resolved to local path (resolved to {})", desc.getLiteralSystemId(), expandedSystemId);
            if (!allowRemoteResources) {
                throw new IllegalStateException(String.format("Identifier %s is not resolved to local path (resolved to %s). Only resources stored in local catalog are enbaled.",
                        desc.getLiteralSystemId(), expandedSystemId));
            }
        }

        if (Log.INSTANCE.isDebugEnabled()) {
            Log.INSTANCE.debug("Resolved identifier: namespace: {} publicId={] systemId={} to {}", xmlResourceIdentifier.getNamespace(), desc.getPublicId(), desc.getLiteralSystemId(), expandedSystemId);
        }
        return id;
    }
}
package org.marketcetera.api.dao;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "permissionAttribute")
public enum PermissionAttribute {
    Create,
    Read,
    Update,
    Delete
}
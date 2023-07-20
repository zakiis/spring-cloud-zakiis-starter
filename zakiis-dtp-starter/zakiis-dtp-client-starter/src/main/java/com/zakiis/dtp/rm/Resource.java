package com.zakiis.dtp.rm;

import io.seata.core.model.BranchType;

public interface Resource {

	/**
     * Get the resource group id.
     * e.g. master and slave data-source should be with the same resource group id.
     *
     * @return resource group id.
     */
    String getResourceGroupId();

    /**
     * Get the resource id.
     * e.g. url of a data-source could be the id of the db data-source resource.
     *
     * @return resource id.
     */
    String getResourceId();

    /**
     * get resource type, AT, TCC, SAGA and XA
     *
     * @return branch type
     */
    BranchType getBranchType();
}

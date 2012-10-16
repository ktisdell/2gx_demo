package com.mycompany.demo.springone.workflow;

import javax.annotation.Resource;

import org.broadleafcommerce.core.inventory.service.InventoryService;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

public class DemoRemoveFlashItemActivity extends BaseActivity {
	
	@Resource(name="blInventoryService")
	protected InventoryService inventoryService;

	@Override
	public ProcessContext execute(ProcessContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}

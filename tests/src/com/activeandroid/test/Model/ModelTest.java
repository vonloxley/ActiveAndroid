package com.activeandroid.test.Model;

/*
 * Copyright (C) 2010 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.activeandroid.query.Select;
import com.activeandroid.test.MockModel;
import com.activeandroid.test.ActiveAndroidTestCase;

public class ModelTest extends ActiveAndroidTestCase {

	public void testGetIdAfterSave(){		
		MockModel m=new MockModel();
		m.MockColumn=42;
		m.save();
		assertNotNull("getId() returned null after save()",m.getId());
		
	}		
	
	public void testGetIdAfterSaveAndSelect(){		
		MockModel m=new MockModel();
		m.MockColumn=42;
		m.save();
		MockModel m2=new Select("MockColumn").from(MockModel.class).where("MockColumn=?", 42).executeSingle();
		assertEquals(42, m.MockColumn); //Check 
		assertNotNull("getId() returned null after Select(\"MockColumn\")",m2.getId());
	}		

}

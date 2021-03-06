/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.metaverse.api.model;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.encryption.TwoWayPasswordEncoderPluginType;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.resource.ResourceEntry;
import org.pentaho.dictionary.DictionaryConst;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExternalResourceInfoFactoryTest {

  @BeforeClass
  public static void init() throws Exception {
    PluginRegistry.addPluginType( TwoWayPasswordEncoderPluginType.getInstance() );
    PluginRegistry.init();
    Encr.init( "Kettle" );

  }

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testDefaultConstructor() {
    assertNotNull( new ExternalResourceInfoFactory() );
  }

  @Test
  public void testCreateDatabaseResource() throws Exception {
    DatabaseMeta dbMeta = mock( DatabaseMeta.class );
    when( dbMeta.getName() ).thenReturn( "myConnection" );
    when( dbMeta.getDescription() ).thenReturn( "Description" );
    DatabaseInterface dbInterface = mock( DatabaseInterface.class );
    when( dbMeta.getDatabaseInterface() ).thenReturn( dbInterface );
    when( dbMeta.getAccessType() ).thenReturn( DatabaseMeta.TYPE_ACCESS_NATIVE );
    when( dbMeta.getAccessTypeDesc() ).thenReturn( "Native" );
    IExternalResourceInfo resourceInfo = ExternalResourceInfoFactory.createDatabaseResource( dbMeta );
    assertTrue( resourceInfo.isInput() );
    resourceInfo = ExternalResourceInfoFactory.createDatabaseResource( dbMeta, false );
    assertFalse( resourceInfo.isInput() );
    when( dbMeta.getAccessType() ).thenReturn( DatabaseMeta.TYPE_ACCESS_JNDI );
    when( dbMeta.getAccessTypeDesc() ).thenReturn( "JNDI" );
    resourceInfo = ExternalResourceInfoFactory.createDatabaseResource( dbMeta );
  }

  @Test
  public void testCreateResource() throws Exception {
    ResourceEntry resourceEntry = mock( ResourceEntry.class );
    when( resourceEntry.getResource() ).thenReturn( "myResource" );
    when( resourceEntry.getResourcetype() ).thenReturn( ResourceEntry.ResourceType.ACTIONFILE );
    IExternalResourceInfo resourceInfo = ExternalResourceInfoFactory.createResource( resourceEntry );
    assertEquals( DictionaryConst.NODE_TYPE_FILE, resourceInfo.getType() );
    when( resourceEntry.getResourcetype() ).thenReturn( ResourceEntry.ResourceType.FILE );
    resourceInfo = ExternalResourceInfoFactory.createResource( resourceEntry, true );
    assertEquals( DictionaryConst.NODE_TYPE_FILE, resourceInfo.getType() );
    when( resourceEntry.getResourcetype() ).thenReturn( ResourceEntry.ResourceType.URL );
    resourceInfo = ExternalResourceInfoFactory.createResource( resourceEntry );
    assertEquals( DictionaryConst.NODE_TYPE_WEBSERVICE, resourceInfo.getType() );
    when( resourceEntry.getResourcetype() ).thenReturn( ResourceEntry.ResourceType.CONNECTION );
    resourceInfo = ExternalResourceInfoFactory.createResource( resourceEntry, false );
    assertEquals( DictionaryConst.NODE_TYPE_DATASOURCE, resourceInfo.getType() );
    when( resourceEntry.getResourcetype() ).thenReturn( ResourceEntry.ResourceType.DATABASENAME );
    resourceInfo = ExternalResourceInfoFactory.createResource( resourceEntry );
    assertEquals( DictionaryConst.NODE_TYPE_DATASOURCE, resourceInfo.getType() );
    when( resourceEntry.getResourcetype() ).thenReturn( ResourceEntry.ResourceType.SERVER );
    resourceInfo = ExternalResourceInfoFactory.createResource( resourceEntry );
    assertEquals( "SERVER", resourceInfo.getType() );
    when( resourceEntry.getResourcetype() ).thenReturn( ResourceEntry.ResourceType.OTHER );
    resourceInfo = ExternalResourceInfoFactory.createResource( resourceEntry );
    assertEquals( "OTHER", resourceInfo.getType() );
  }

  @Test
  public void testCreateFileResource() throws Exception {
    assertNull( ExternalResourceInfoFactory.createFileResource( null ) );
    FileObject mockFile = mock( FileObject.class );
    FileName mockFilename = mock( FileName.class );
    when( mockFilename.getPath() ).thenReturn( "/path/to/file" );
    when( mockFile.getName() ).thenReturn( mockFilename );
    IExternalResourceInfo resource = ExternalResourceInfoFactory.createFileResource( mockFile, false );
    assertNotNull( resource );
    assertEquals( "/path/to/file", resource.getName() );
    assertFalse( resource.isInput() );
  }
}

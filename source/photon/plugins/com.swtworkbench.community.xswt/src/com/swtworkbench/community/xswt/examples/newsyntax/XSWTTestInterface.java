/*******************************************************************************
 * Copyright (c) 2000, 2003 Advanced Systems Concepts, Inc.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     David Orme (ASC) - Initial implementation
 ******************************************************************************/
package com.swtworkbench.community.xswt.examples.newsyntax;

import java.io.IOException;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.swtworkbench.community.xswt.XSWT;
import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.metalogger.Logger;

/**
 * Class XSWTTest.
 * 
 * @author daveo
 */
public class XSWTTestInterface {

   public static void main(String[] args) throws IOException {
      Display display = new Display();
      Shell shell = new Shell(display);
      IHelloWorld results = null;

      try {
         results = (IHelloWorld) 
            XSWT.create(XSWTTestInterface.class.getResourceAsStream("HelloWorld.xswt"))
                .parse(shell, IHelloWorld.class);
      } catch (XSWTException e) {
         Logger.log().error(e, "Unable to parse XSWT file");
      }

      shell.setSize(600, 600);
      shell.open();
      while (!shell.isDisposed()) {
         if (!display.readAndDispatch())
            display.sleep();
      }
      display.dispose();
   }
}

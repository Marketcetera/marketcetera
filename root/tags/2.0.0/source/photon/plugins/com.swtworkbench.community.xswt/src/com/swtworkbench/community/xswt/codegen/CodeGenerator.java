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
package com.swtworkbench.community.xswt.codegen;

import java.util.HashMap;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.swtworkbench.community.xswt.codegens.CharacterCodeGenerator;
import com.swtworkbench.community.xswt.codegens.FontCodeGenerator;
import com.swtworkbench.community.xswt.codegens.ImageCodeGenerator;
import com.swtworkbench.community.xswt.codegens.IntArrayCodeGenerator;
import com.swtworkbench.community.xswt.codegens.ObjectStubCodeGenerator;
import com.swtworkbench.community.xswt.codegens.PointCodeGenerator;
import com.swtworkbench.community.xswt.codegens.RGBCodeGenerator;
import com.swtworkbench.community.xswt.codegens.RectangleCodeGenerator;
import com.swtworkbench.community.xswt.codegens.StringArrayCodeGenerator;
import com.swtworkbench.community.xswt.codegens.StringCodeGenerator;
import com.swtworkbench.community.xswt.layoutbuilder.ObjectStub;

/**
 * Class CodeGenerator.  
 * 
 * @author djo
 */
public class CodeGenerator implements ICodeGenerator {

    private static CodeGenerator generator = null;
    
    public static CodeGenerator getDefault() {
        if (generator == null) generator = new CodeGenerator();
        return generator;
    }
    
    private static HashMap constantGenerators = new HashMap();
    
    /**
     * Method registerGenerator.  Register a class that can convert from an 
     * object to a Java constant expression representing the object's value
     * 
     * @param klass The Class object representing the type to convert
     * @param generator The object that can return a String from an object of that type
     */
    public static void registerGenerator(Class klass, ICodeGenerator generator) {
        constantGenerators.put(klass, generator);
    }
    

    /* (non-Javadoc)
     * @see net.sf.sweet_swt.xswt.constantgen.IConstantGenerator#getConstant(java.lang.Object)
     */
    public String getCode(Object o, String source) {
        ICodeGenerator generator = (ICodeGenerator)constantGenerators.get(o.getClass());
        /*
         * Many things--Integers, Floats, etc--can be generated perfectly well by calling .toString()
         */
        if (generator == null)
            return o.toString();
        return generator.getCode(o, source);
    }

    static {
		CodeGenerator.registerGenerator(Character.TYPE,
				new CharacterCodeGenerator());
		CodeGenerator
				.registerGenerator(String.class, new StringCodeGenerator());
		CodeGenerator.registerGenerator(Font.class, new FontCodeGenerator());
		CodeGenerator.registerGenerator(Image.class, new ImageCodeGenerator());
		CodeGenerator.registerGenerator(int[].class,
				new IntArrayCodeGenerator());
		CodeGenerator.registerGenerator(ObjectStub.class,
				new ObjectStubCodeGenerator());
		CodeGenerator.registerGenerator(Point.class, new PointCodeGenerator());
		CodeGenerator.registerGenerator(Rectangle.class,
				new RectangleCodeGenerator());
		CodeGenerator.registerGenerator(RGB.class, new RGBCodeGenerator());
		CodeGenerator.registerGenerator(String[].class,
				new StringArrayCodeGenerator());
    }
}

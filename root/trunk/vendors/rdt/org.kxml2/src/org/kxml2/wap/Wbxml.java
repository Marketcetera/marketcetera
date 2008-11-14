/* kXML 2
 *
 * Copyright (C) 2000, 2001, 2002 
 *               Stefan Haustein
 *               D-46045 Oberhausen (Rhld.),
 *               Germany. All Rights Reserved.
 *
 * The contents of this file are subject to the "Common Public
 * License" (CPL); you may not use this file except in compliance
 * with the License.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific terms governing rights and limitations
 * under the License.
 *
 * Thanks to Paul Palaszewski, Wilhelm Fitzpatrick, 
 * Eric Foster-Johnson, Michael Angel, and Liam Quinn for providing various
 * fixes and hints for the KXML 1 parser.
 * */
package org.kxml2.wap;


/** contains the WBXML constants  */


public interface Wbxml {

    static public final int SWITCH_PAGE = 0;
    static public final int END = 1;
    static public final int ENTITY = 2;
    static public final int STR_I = 3;
    static public final int LITERAL = 4;
    static public final int EXT_I_0 = 0x40;
    static public final int EXT_I_1 = 0x41;
    static public final int EXT_I_2 = 0x42;
    static public final int PI = 0x43;
    static public final int LITERAL_C = 0x44;
    static public final int EXT_T_0 = 0x80;
    static public final int EXT_T_1 = 0x81;
    static public final int EXT_T_2 = 0x82;
    static public final int STR_T = 0x83;
    static public final int LITERAL_A = 0x084;
    static public final int EXT_0 = 0x0c0;
    static public final int EXT_1 = 0x0c1;
    static public final int EXT_2 = 0x0c2;
    static public final int OPAQUE = 0x0c3; 
    static public final int LITERAL_AC = 0x0c4;
}

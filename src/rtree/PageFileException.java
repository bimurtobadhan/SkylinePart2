/* 
 *
 * webEIEL
 * Sistema de Informaci�n Xeogr�fica da Deputaci�n de A Coru�a
 *
 * Este documento pode consultarse en galego nesta URL:
 *    http://www.dicoruna.es/webeiel/giseiel/licencia-giseiel_gl.do
 *
 * Este documento puede consultarse en castellano en esta URL:
 *    http://www.dicoruna.es/webeiel/giseiel/licencia-giseiel_es.do
 *
 * Copyright (C) 2008-2011 Deputaci�n de A Coru�a
 *
 * This software was developed by the Databases Laboratory of 
 * the University of A Coru�a (http://lbd.udc.es). The authors of 
 * the software are enumerated in a file named AUTHORS.txt located 
 * on the root of the project.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *   Pedro A. Gonz�lez P�rez (pedro.gonzalez@dicoruna.es)
 *   Asistencia T�cnica A Municipios
 *   Deputaci�n Provincial de A Coru�a
 *   Avda. Alf�rez Provisional, s/n
 *   15008 A Coru�a
 *   Spain
 *
 *   Miguel R. Luaces (luaces@udc.es)
 *   Databases Laboratory
 *   Facultade de Inform�tica
 *   Universidade da Coru�a
 *   15071 A Coru�a
 *   Spain
 *
 */
//RTree implementation.
//Copyright (C) 2002-2004 Wolfgang Baer - WBaer@gmx.de
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this library; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package rtree;

/**
 * <p>
 * Exception class for all exceptions regarding pagefiles
 * </p> 
 * @author Wolfgang Baer - WBaer@gmx.de
 */
public class PageFileException extends Exception {

	/**
	 * Constructor for PageFileException.
	 */
	public PageFileException() {
		super();
	}

	/**
	 * Constructor for PageFileException.
	 * @param message
	 */
	public PageFileException(String message) {
		super(message);
	}
}

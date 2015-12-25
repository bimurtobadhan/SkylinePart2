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

import java.util.Hashtable;


/**
 * <p>
 * A memory based implementation of a PageFile.<br>
 * Implemented as a Hashtable with keys representing
 * the page file numbers of the saved nodes.
 * </p>
 * @author Wolfgang Baer - WBaer@gmx.de
 */
public class MemoryPageFile extends PageFile {
	
	private Hashtable file = new Hashtable();
		
	/**
	 * Constructor
	 */
	protected MemoryPageFile() {
		super();
		file.clear();
	}

	/**
	 * @see PageFile#readNode(int)
	 */
	public Node readNode(int pageFile) throws PageFileException {
		return (Node)file.get(new Integer(pageFile));
	}

	/**
	 * @see PageFile#writeNode(Node)
	 */
	protected int writeNode(Node node) throws PageFileException {
		
		int i = 0;
		if (node.getPageNumber() < 0) {
	    	while (true) {
				if (! file.containsKey(new Integer(i))) {
		    		break;
				}
				i++;
	    	}
	    	node.setPageNumber(i);
		} 
		else 
		    i = node.getPageNumber();	
		    
		file.put(new Integer(i), node);		
		
		return i;
	}
	
	/**
	 * @see PageFile#deleteNode(int)
	 */
	protected Node deleteNode(int pageNumber) {
		return  (Node)file.remove(new Integer(pageNumber));
	}
	
	/**
	 * @see PageFile#close()
	 */
	protected void close() throws PageFileException {		
		// nothing to do
	}
}

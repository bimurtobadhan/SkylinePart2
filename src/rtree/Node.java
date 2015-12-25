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
 * Abstract class for common implementation and definition of abstract methods
 * for both concrete classes LeafNode and NoneLeafNode.
 * </p>
 * @author Wolfgang Baer - WBaer@gmx.de
 */
public abstract class Node {
	
	protected transient PageFile file;
	protected int parentNode;
	protected int pageNumber;
	protected int counter;
	protected HyperBoundingBox unionMinBB;
	protected HyperBoundingBox[] hyperBBs;
	protected int place;
	
	/** 
	 * Constructor.
	 * @param pageNumber - number of this node in page file
	 * @param pageFile - the PageFile of this node
	 */	
	public Node(int pageNumber, PageFile pageFile) {
		this.file = pageFile;
		this.pageNumber = pageNumber;
		parentNode = 0;
		hyperBBs = new HyperBoundingBox[file.getCapacity()];
		for(int i=0; i < file.getCapacity(); i++)
			hyperBBs[i] = HyperBoundingBox.getNullHyperBoundingBox(file.getDimension());
		unionMinBB = HyperBoundingBox.getNullHyperBoundingBox(file.getDimension());
		counter = 0;
	}
	
	/**
	 * Inserts the given data into the node
	 * @param obj - object to insert (Typ Integer oder AbstractNode)
	 * @param box - the associated HyperBoundingBox
	 */
	protected abstract void insertData(Object obj, HyperBoundingBox box); 
	
	/** 
	 * Deletes a the entry with given index from node
	 * @param index - index of entry
	 */
	protected abstract void deleteData(int index);				
	
	/**
	 * Fetches the data for given index from node
	 * @param index - index of data
	 */
	public abstract Object getData(int index);

	/**
	 * Returns the parent node of this.
	 * @return Node
	 */
	public Node getParent() {
		
		Node node = null;		
		try {
			node = file.readNode(parentNode);	
		} catch(PageFileException e) {
			System.out.println("PageFileException: AbstractNode.getParent() - readNode");
		}			
		return node;
	}
	
	/** 
	 * Returns the page number of this.
	 * @return int
	 */
	protected int getPageNumber() {
		return pageNumber;
	}
	
	/** 
	 * Sets the page number of this to given number
	 * @param number - int
	 */
	protected void setPageNumber(int number) {
		this.pageNumber = number;
	}

	/**
	 * Currently used space in the node
	 * @return int
	 */
	public int getUsedSpace() {
		return counter;
	}

	/**
	 * Returns the HyperBoundingBox over all Entries currently in the node
	 * @return HyperBoundingBox 
	 */
	public HyperBoundingBox getUnionMinBB() {
		return unionMinBB;
	}
	
	/**
	 * Updates the HyperBoundingBox over all Entries currently in the node
	 */
	protected void updateNodeBoundingBox() {
		this.unionMinBB = HyperBoundingBox.getNullHyperBoundingBox(file.getDimension());;
		for(int i=0; i < this.getUsedSpace(); i++)
			this.unionMinBB = this.unionMinBB.unionBoundingBox(this.hyperBBs[i]);			
	}

	/**
	 * Returns an array of HyperBoundingBox objects of the entries of the node.
	 * The array may be empty - for used place in the node see getUsedSpace.
	 * @return HyperBoundingBox[] - boxes of the entries
	 * @see #getUsedSpace()
	 */
	public HyperBoundingBox[] getHyperBoundingBoxes() {
		return hyperBBs;
	}
	
	/**
	 * Returns the HyperBoundingBox for entrie with given index.
	 * @param index - index of entry
	 * @return HyperBoundingBox
	 */
	public HyperBoundingBox getHyperBoundingBox(int index) {
		return hyperBBs[index];
	}
	
	/**
	 * Tests if this is the root node.
	 * @return boolean
	 */
	public boolean isRoot() {
		return pageNumber == 0;
	}
	
	/**
	 * Deep copy without data entries (only HyperBoundingBox objects)
	 * @see Object#clone()
	 */
	protected abstract Object clone();
	
	/**
	 * String-Representation of Node
	 */
	public String toString() {
		String str = "";
		if(this instanceof LeafNode)
			str = "LeafNode: "+unionMinBB.toString();
		else 
			str = "NoneLeafNode: "+unionMinBB.toString();
		return str;
	}	
}

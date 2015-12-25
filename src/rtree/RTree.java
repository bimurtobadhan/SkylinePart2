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

import java.util.*;


/**
 * <br>
 * Implementation of a R-Tree after the algorithms of Antonio Guttman.
 * With nearest neighbour search after algorithm of Cheung & Fu
 * <br>
 * @author Wolfgang Baer - WBaer@gmx.de
 */
public class RTree {
		
	private PageFile file;
			
	/** 
	 * Creates an empty R-Tree with a memory-mapped pagefile (MemoryPageFile)
	 * and an empty root node
	 * @param dimension - dimension of the data to store
	 * @param maxLoad - maximum load of a node 
	 **/
	public RTree(int dimension, int maxLoad) throws RTreeException {
		this.file = new MemoryPageFile();
				
		try {
			file.initialize(dimension, maxLoad + 1);
			Node rootNode = new LeafNode(0, this.file);
			file.writeNode(rootNode);
		} catch(PageFileException e) {
			e.fillInStackTrace();
			throw new RTreeException("PageFileException in constructor occured");
		}				
	}
	
	/** 
	 * Creates an empty R-Tree with a persistent pagefile (PersistentPageFile)
	 * and an empty root node.
	 * @param dimension - dimension of the data to store
	 * @param maxLoad - maximum load of a node	  
	 * @param fileName - name of the rtree file
	 */
	public RTree(int dimension, int maxLoad, String fileName) throws RTreeException {
		try {
			this.file = new PersistentPageFile(fileName);
			this.file.initialize(dimension, maxLoad+1);
			
			Node rootNode = new LeafNode(0, this.file);
			file.writeNode(rootNode);
		}
		catch (PageFileException e) {
			e.fillInStackTrace();
			throw new RTreeException("PageFileException in constructor occured");
		}		
	}
	
	/**
	 * Creates an R-Tree from an EXISTING persistent pagefile (PersistentPageFile).
	 * @param fileName - name of the existing rtree file
	 */
	public RTree(String fileName) throws RTreeException {
		
		this.file = new PersistentPageFile(fileName);
		try {
			this.file.initialize(-999, -999);	
		}
		catch (PageFileException e) {
			e.fillInStackTrace();
			throw new RTreeException("PageFileException in constructor occured");
		}		
	}
	
	
	/**
	 * Returns a (newly created) memory-mapped R-Tree for the specified HyperBoundingBox with given properties.
	 * @param box - HyperBoundingBox the area of the new rtree.
	 * @param maxLoad - maximum load of a node	  
	 * @return RTree - newly created R-Tree
	 */
	public RTree getMemoryRTree(HyperBoundingBox box, int maxLoad) throws RTreeException {
		
		if(box.getDimension() != file.getDimension())
			throw new IllegalArgumentException("HyperBoundingBox must have same dimension as existing rtree");
				
		Vector v = new Vector();
		
		try {
			copySearchNodes(file.readNode(0), v, box);
		} catch(PageFileException e) {
			e.fillInStackTrace();
			throw new RTreeException("PageFileException RTree.search() - readNode()");
		}
		
		RTree tree = new RTree(file.getDimension(), maxLoad);
		
		for(Enumeration e = v.elements(); e.hasMoreElements(); ) {
			Data data = (Data)e.nextElement();
			tree.insert(data.getInteger(), data.getHyperBoundingBox());			
		}
				
		return tree;
	}
	

	private void copySearchNodes(Node node1, Vector v, HyperBoundingBox box) {
		if(node1 instanceof LeafNode) {			
			LeafNode node = (LeafNode) node1;
							
			for(int i=0; i < node.getUsedSpace(); i++) {
				// if entries overlap - put them in the Vector
				if(node.hyperBBs[i].overlaps(box)) {					
					Integer intData = new Integer(((Integer)node.getData(i)).intValue());
					HyperBoundingBox hbox = (HyperBoundingBox) node.hyperBBs[i].clone();
					v.addElement(new Data(intData, hbox));
				}
			}			
			return;
		}
		else {					
			NoneLeafNode node = (NoneLeafNode) node1;
			
			// as node is no LeafNode
			// search every entries for overlapping
			for(int i=0; i < node.getUsedSpace(); i++) {
				// if overlaps - search recursive with the current node
				if(node.hyperBBs[i].overlaps(box)) {
					copySearchNodes((Node)node.getData(i), v, box);
				}
			}				
		}
	} 
	
	/**
	 * Searches all entries in the R-Tree whose HyperBoundingBoxes intersect with the given.
	 * @param box - given test HyperBoundingBox
	 * @return Object[] - Array with retrieved Objects
	 */
	public Object[] intersects(HyperBoundingBox box) throws RTreeException {
		if(box.getDimension() != file.getDimension())
			throw new IllegalArgumentException("HyperBoundingBox has wrong dimension");
		
		Vector v = new Vector();
		// calls the real search method
		try {
			intersectsSearch(file.readNode(0), v, box);
		} catch(PageFileException e) {
			e.fillInStackTrace();
			throw new RTreeException("PageFileException RTree.search() - readNode()");			
		}
		return v.toArray();	
	}
		
	/**
	 * Searches all entries in the R-Tree whose HyperBoundingBoxes contain the given.
	 * @param box - given test HyperBoundingBox
	 * @return Object[] - Array with retrieved Objects
	 */	
	public Object[] contains(HyperBoundingBox box) throws RTreeException {
		if(box.getDimension() != file.getDimension())
			throw new IllegalArgumentException("HyperBoundingBox has wrong dimension");
		
		Vector v = new Vector();
		try {
			containsSearch(file.readNode(0), v, box);
		} catch(PageFileException e) {
			e.fillInStackTrace();
			throw new RTreeException("PageFileException RTree.search() - readNode() ");			
		}
		return v.toArray();
	}
	
	// private method for contains search
	private void containsSearch(Node node1, Vector v, HyperBoundingBox box) {
		if(node1 instanceof LeafNode) {			
			LeafNode node = (LeafNode) node1;
							
			for(int i=0; i < node.getUsedSpace(); i++) {
				// if box is contained put into Vector
				if(node.hyperBBs[i].contains(box))
					v.addElement(node.getData(i));
			}			
			return;
		}
		else {					
			NoneLeafNode node = (NoneLeafNode) node1;
			
			// node is no Leafnode - so search all entries for overlapping
			for(int i=0; i < node.getUsedSpace(); i++) {
				if(node.hyperBBs[i].contains(box)) 
					containsSearch((Node)node.getData(i), v, box);				
			}				
		}
	}
	
	// private method for intersects search
	private void intersectsSearch(Node node1, Vector v, HyperBoundingBox box) {
		if(node1 instanceof LeafNode) {			
			LeafNode node = (LeafNode) node1;
							
			for(int i=0; i < node.getUsedSpace(); i++) {
				if(node.hyperBBs[i].overlaps(box))
					v.addElement(node.getData(i));
			}			
			return;
		}
		else {					
			NoneLeafNode node = (NoneLeafNode) node1;			
			
			for(int i=0; i < node.getUsedSpace(); i++) {
				if(node.hyperBBs[i].overlaps(box)) 
					intersectsSearch((Node)node.getData(i), v, box);				
			}				
		}
	}
	
	/**
	 * Inserts the given Object associated with the given HyperBoundingBox object into the R-Tree.
	 * @param obj - Object to insert
	 * @param box - associated HyperBoundingBox
	 * @return boolean - true if successfull
	 */
	public boolean insert(Object obj, HyperBoundingBox box) throws RTreeException {
		
		try {
			Node[] newNodes = new Node[] {null, null};
			//Find position for new record
			LeafNode node;
			node = chooseLeaf(file.readNode(0), box);
			
			// Add record to leaf node
			
			if( node.getUsedSpace() < (file.getCapacity() -1) ) {		
				node.insertData(obj, box);
				file.writeNode(node);			
			}
			else {
				// invoke SplitNode				
				node.insertData(obj, box);
				file.writeNode(node);			
				newNodes = splitNode(node);
			}
			
			if(newNodes[0] != null) {
				adjustTree(newNodes[0], newNodes[1]);
			}
			else	{
				adjustTree(node, null);
			}
		} catch(PageFileException e) {
			e.fillInStackTrace();
			throw new RTreeException("PageFileException occured");			
		}
		
		return true;
	}
	
	// algorithm to split a full node
	private Node[] splitNode(Node node) throws PageFileException {
			
		// new node
		Node newNode = null;
		// temp help node
		Node helpNode = null;		
						
		// compute the start entries 		
		int[] seeds = pickSeeds(node);
						
		if(node instanceof LeafNode) {
			newNode = new LeafNode(this.file);
			helpNode = new LeafNode(node.getPageNumber(), this.file);
		}
		else {
			newNode = new NoneLeafNode(-1,this.file);
			helpNode = new NoneLeafNode(node.getPageNumber(), this.file);			
		}
		
		// write the new node to pagefile
		file.writeNode(newNode);
				
		node.counter = 0;
		node.unionMinBB = HyperBoundingBox.getNullHyperBoundingBox(file.getDimension());
		
		// insert the start entries
		helpNode.insertData(node.getData(seeds[0]), node.getHyperBoundingBox(seeds[0]));
		newNode.insertData(node.getData(seeds[1]), node.getHyperBoundingBox(seeds[1]));
			
		// mark the inserted entries - first build a marker array
		boolean[] marker = new boolean[file.getCapacity()];
		for(int i = 0; i < file.getCapacity(); i++)
			marker[i] = false;
		
		// mark them
		marker[seeds[0]] = true;
		marker[seeds[1]] = true;
			
		int doneCounter = file.getCapacity() - 2;	
				
		// do until all entries are put into one of the groups or until
		// one group has so less entries that the remainder must be given to that group
		while(doneCounter > 0) {
			int[] entry;
			entry = pickNext(node, marker, helpNode, newNode);
			doneCounter --;
			if(entry[0] == 1) 
				helpNode.insertData(node.getData(entry[1]), node.getHyperBoundingBox(entry[1]));								 
			else
				newNode.insertData(node.getData(entry[1]), node.getHyperBoundingBox(entry[1]));								 
						
			if((file.getMinimum() - helpNode.getUsedSpace()) == doneCounter) {
				
				for(int i=0; i < file.getCapacity(); i++)
					if(marker[i] == false)
						helpNode.insertData(node.getData(i), node.getHyperBoundingBox(i));								 				
				break;
			}	
			
			if((file.getMinimum() - newNode.getUsedSpace()) == doneCounter) {
				
				for(int i=0; i < file.getCapacity(); i++)
					if(marker[i] == false)
						newNode.insertData(node.getData(i), node.getHyperBoundingBox(i));								 				
				break;
			}							
		} 
		
		// put the entries from the temp node to current node
		for(int x= 0; x < helpNode.getUsedSpace(); x++)
			node.insertData(helpNode.getData(x), helpNode.getHyperBoundingBox(x));		
					
		file.writeNode(node);
		file.writeNode(newNode);			
		
		return new Node[] {node, newNode};
	}
	
	// picks the first to entries for the new nodes - returns the index of the entries
	private int[] pickSeeds(Node node) {
		
		double max = 0.0;
		int e1 = 0;
		int e2 = 0;
		
		// walks through all combinations and takes
		// the combination with the largest area enlargement
		for(int i=0; i < file.getCapacity(); i++) 
			for(int j=0; j < file.getCapacity(); j++) {
				if(i!=j) {
					double d = (node.getHyperBoundingBox(i)).unionBoundingBox(node.getHyperBoundingBox(j)).getArea() - node.getHyperBoundingBox(i).getArea() - node.getHyperBoundingBox(j).getArea();
					if(d > max) {
						max = d;
						e1 = i;
						e2 = j;
					}
				}							
			}
		return new int[] {e1,e2};
	}
	
	// int[0] = group, int[1] = entry
	private int[] pickNext(Node node, boolean[] marker, Node group1, Node group2) {
		
		double d0 = 0;
		double d1 = 0;
		double diff = -1;
		double max = -1;
		int entry = 99;
		int group = 99;
		
		for(int i=0; i < file.getCapacity(); i++) {			
			if(marker[i] == false) {				
				d0 = group1.getUnionMinBB().unionBoundingBox(node.getHyperBoundingBox(i)).getArea() - group1.getUnionMinBB().getArea();				
				
				d1 = group2.getUnionMinBB().unionBoundingBox(node.getHyperBoundingBox(i)).getArea() - group2.getUnionMinBB().getArea();	
				diff = Math.abs(d0 - d1);
				if(diff > max) {
					if(d0 < d1) 
						group = 1;
					else
						group = 2;
					max = diff;
					entry = i;
				}
				if(diff == max) {
					if(d0 < d1) 
						group = 1;
					else
						group = 2;
					max = diff;
					entry = i;
				}					
			}
		}		

		marker[entry] = true;		
		return new int[] {group, entry};
	}
	
	// searches the leafnode with LeastEnlargment criterium for insert
	private LeafNode chooseLeaf(Node node, HyperBoundingBox box) {
		
		if(node instanceof LeafNode) {
			return (LeafNode)node;
		}
		else {
			NoneLeafNode node1 = (NoneLeafNode) node;
			int least = node1.getLeastEnlargement(box);
			return chooseLeaf((Node)node1.getData(least), box);	
		}
	}
	

	/**
	 * Queries the nearest neighbour to given search HyperPoint
	 * @param point - search point
	 * @return double[] - Place 0 = Distance, Place 1 = data number (must be cast to int)
	 */
	public double[] nearestNeighbour(HyperPoint point) throws RTreeException {
		try {
			return nearestNeighbour(file.readNode(0),point, new double[] {Double.POSITIVE_INFINITY, -1.0});
		} catch(PageFileException e) {
			e.fillInStackTrace();
			throw new RTreeException("PageFileException - nearestNeighbour - readNode(0)");
		}		
	}
	
	// private method for nearest neighbour query
	private double[] nearestNeighbour(Node node, HyperPoint point, double[] temp) {


		if(node instanceof LeafNode) {
			// if mindist this < tempDist
			for(int i = 0; i < node.getUsedSpace(); i++) {
				double dist = node.getHyperBoundingBox(i).minDist(point);
				if(dist < temp[0]) {					
					// then this = nearest Neighbour - update tempDist
					temp[1] = ((LeafNode)node).data[i];
					temp[0] = dist;
					System.out.println(node.getHyperBoundingBox(i)+" "+Math.sqrt(dist)+" "+((LeafNode)node).data[i]);
				}
			}
		}
		else {
			// generate ActiveBranchList of node
			ABL[] abl = new ABL[node.getUsedSpace()];
			for(int i=0; i < node.getUsedSpace(); i++) {
				Node help = (Node)node.getData(i);
				abl[i] = new ABL(help, help.getUnionMinBB().minDist(point));							
			}
			
			//sort activebranchlist
			Arrays.sort(abl);			
			
			for(int i=0; i < abl.length; i++) {
				// apply heuristic 3
				System.out.println(abl[i].minDist);
				if(abl[i].minDist <= temp[0]) {
					temp = nearestNeighbour(abl[i].node, point, temp);
				}
			}			
		}
		return temp;		
	}		
	
	/**
	 * Closes the rtree.
	 * @throws RTreeException - if an error occures.
	 */
	public void close() throws RTreeException {
		try {
			file.close();
		}
		catch (PageFileException e) {
			e.fillInStackTrace();
			throw new RTreeException("PageFileException - close()");		
		}
	}
		
	
	/**
	 * Deletes an entry from the RTree.
	 * @param box - HyperBoundingBox of the entry to deleted
	 * @param objID - Integer value of Object-ID to be deleted	
	 * @return boolean - true if successful
	 */
	public boolean delete(HyperBoundingBox box, int objID) throws RTreeException {
			
		Vector v = new Vector();
		try {
			findLeaf(file.readNode(0),box, objID, v);
		} catch(PageFileException e) {
			e.fillInStackTrace();
			throw new RTreeException("PageFileException - delete()");						
		}
		
		if(v.size() < 1)
			return false;
			
		if(v.size() == 1) {
			
			LeafNode leaf = (LeafNode) v.elementAt(0);
			
			for(int i = 0; i < leaf.getUsedSpace(); i++) {
				if(leaf.getHyperBoundingBox(i).equals(box) && leaf.data[i] == objID) {
					leaf.deleteData(i);					
					
					try {
						file.writeNode(leaf);
					} catch(PageFileException e) {						
						e.fillInStackTrace();
						throw new RTreeException("PageFileException - delete()");			
					}
				}
			}
			
			Stack stack = new Stack();
			try {
				condenseTree(leaf, stack);
			}
			catch (PageFileException e) {
				e.fillInStackTrace();
				throw new RTreeException("PageFileException - condenseTree()");
			}
			
			while(!stack.empty()) {
		
				Node node = (Node)stack.pop();
				
				if(node instanceof LeafNode) {
					for(int i = 0; i < node.getUsedSpace(); i++)
						this.insert(((LeafNode)node).getData(i), ((LeafNode)node).getHyperBoundingBox(i));
				}
				else {
					for(int i = 0; i < node.getUsedSpace(); i++)
						stack.push(((NoneLeafNode)node).getData(i));						
				}
				try {
					file.deleteNode(node.pageNumber);
				} catch(PageFileException e) {
					e.fillInStackTrace();
					throw new RTreeException("PageFileException - delete() - deleteNode(0)");
				}				
			}							
		}	
		return true;
	}
	
	/**
	 * Deletes all entries from the R-Tree with given HyperBoundingBox
	 * @param box - HyperBoundingBox
	 * @return boolean - true if successfull
	 */
	public boolean delete(HyperBoundingBox box) throws RTreeException {
			
		Vector v = new Vector();
		try {
			findLeaf(file.readNode(0),box, v);
		} catch(PageFileException e) {
			e.fillInStackTrace();
			throw new RTreeException("PageFileException - delete()");						
		}
		
		if(v.size() < 1) 
			return false;		
		else {
			
			LeafNode leaf;
			
			for(Enumeration en = v.elements(); en.hasMoreElements(); ) {
				
				leaf = (LeafNode) en.nextElement();
			
				for(int i = 0; i < leaf.getUsedSpace(); i++) {
					if(leaf.getHyperBoundingBox(i).equals(box)) {
						leaf.deleteData(i);					
						
						try {
							file.writeNode(leaf);
						} catch(PageFileException e) {						
							e.fillInStackTrace();
							throw new RTreeException("PageFileException - delete()");			
						}
					}
				}
			
				Stack stack = new Stack();
				try {
					condenseTree(leaf, stack);
				}
				catch (PageFileException e) {
					e.fillInStackTrace();
					throw new RTreeException("PageFileException - condenseTree()");
				}
				
				while(!stack.empty()) {
		
					Node node = (Node)stack.pop();
				
					if(node instanceof LeafNode) {
						for(int i = 0; i < node.getUsedSpace(); i++)
							this.insert(((LeafNode)node).getData(i), ((LeafNode)node).getHyperBoundingBox(i));
					}
					else {
						for(int i = 0; i < node.getUsedSpace(); i++)
							stack.push(((NoneLeafNode)node).getData(i));						
					}
					try {
						file.deleteNode(node.pageNumber);
					} catch(PageFileException e) {
						e.fillInStackTrace();
						throw new RTreeException("PageFileException - delete() - deleteNode(0)");
					}				
				}							
			}
		}	
		return true;
	}
	
	/**
	 * Retrieves all entries with the given HyperBoundingBox.
	 * @param box - HyperBoundingBox
	 * @return Object[] - array with retrieved objects
	 */
	public Object[] find(HyperBoundingBox box) throws RTreeException {
		if(box.getDimension() != file.getDimension())
			throw new IllegalArgumentException("HyperBoundingBox has wrong dimension");
		
		Vector v = new Vector();
		// ruft die eigentliche suche auf
		try {
			findSearch(file.readNode(0), v, box);
		} catch(PageFileException e) {
			e.fillInStackTrace();
			throw new RTreeException("PageFileException RTree.search() - readNode()");			
		}
		return v.toArray();	
	}
	
	// F�hrt die eigentliche Suche durch - Aufruf von search(HyperBoundingBox box)
	private void findSearch(Node node1, Vector v, HyperBoundingBox box) {
		if(node1 instanceof LeafNode) {			
			LeafNode node = (LeafNode) node1;
							
			for(int i=0; i < node.getUsedSpace(); i++) {
				// wenn eintr�ge enthalten diese in Vechtor aufnehmen;
				if(node.hyperBBs[i].equals(box))
					v.addElement(node.getData(i));
			}			
			return;
		}
		else {					
			NoneLeafNode node = (NoneLeafNode) node1;
			
			// node ist kein LeafNode
			// alle eintra�ge auf �berlappung durchsuchen
			for(int i=0; i < node.getUsedSpace(); i++) {
				// wenn enthalten rekursiv search mit diesem node aufrufen
				if(node.hyperBBs[i].contains(box)) {
					findSearch((Node)node.getData(i), v, box);
				}
			}				
		}
	}

	private void findLeaf(Node node, HyperBoundingBox box, int objID, Vector v) {
		if(node instanceof LeafNode) {
			for(int i = 0; i < node.getUsedSpace(); i++) {
				if(((LeafNode)node).data[i] == objID && node.getHyperBoundingBox(i).equals(box))
					v.addElement((LeafNode)node);
			}
		}
		else {
			for(int i = 0; i < node.getUsedSpace(); i++) {
				if(node.getHyperBoundingBox(i).overlaps(box))
					findLeaf((Node)node.getData(i), box, objID, v);
			}
		}
	}

	// Retrieves all leaf nodes regardless of the id
	private void findLeaf(Node node, HyperBoundingBox box, Vector v) {
		if(node instanceof LeafNode) {
			for(int i = 0; i < node.getUsedSpace(); i++) {
				if(node.getHyperBoundingBox(i).equals(box))
					v.addElement((LeafNode)node);
			}
		}
		else {
			for(int i = 0; i < node.getUsedSpace(); i++) {
				if(node.getHyperBoundingBox(i).overlaps(box))
					findLeaf((Node)node.getData(i), box, v);
			}
		}
	}
	
    // Retrieves all leaf nodes with correct box and id

	// condenses the tree after remove of some entries
	private void condenseTree(Node n, Stack stack) throws PageFileException {
		if(!n.isRoot()) {

			Node p = n.getParent();
			if(n.getUsedSpace() < file.getMinimum()) {
				p.deleteData(n.place);
				stack.push(n);
			}
			else {
				p.hyperBBs[n.place] = n.getUnionMinBB();
				p.updateNodeBoundingBox();
			}

			file.writeNode(p);

			condenseTree(p, stack);
		}
		else {
			if(n.getUsedSpace() == 1 && (n instanceof NoneLeafNode)) {

				Node kind = (Node)n.getData(0);
				Node newRoot = null;
				if(kind instanceof LeafNode) {
					newRoot = new LeafNode(0, this.file);
					for(int i=0; i < kind.getUsedSpace(); i++)
						newRoot.insertData(kind.getData(i), kind.getHyperBoundingBox(i));
				}
				else {
					newRoot = new NoneLeafNode(0, this.file);
					for(int i=0; i < kind.getUsedSpace(); i++)
						newRoot.insertData(kind.getData(i), kind.getHyperBoundingBox(i));
				}

				file.writeNode(newRoot);
			}
		}
	}

	// adjustes the Tree with the correct bounding boxes and
	// propagates needed splits upwards
	private void adjustTree(Node n1, Node n2) throws PageFileException {
		// if n2 = null - only adjust boundingboxes
		// if n2 != null a split occured - maybe propagate split

		if(n1.isRoot()) {

			// if n2 !U null we need a new Root node - Root Split
			if(n2 != null && n1.isRoot()) {

				// Node must be written from page number 0 (root number) to other
				n1.setPageNumber(-1);
				int pagenumber;

				pagenumber = file.writeNode(n1);

				for(int x = 0; x < n1.getUsedSpace(); x++)  {
					Object obj = n1.getData(x);
					if(obj instanceof Node) {
						Node node = (Node)obj;
						node.parentNode = pagenumber;
						file.writeNode(node);
					}
					obj = null;
				}

		    	NoneLeafNode newRoot = new NoneLeafNode(0, this.file);

		   	 	newRoot.insertData(n1, n1.getUnionMinBB());
		   		newRoot.insertData(n2, n2.getUnionMinBB());
		   	 	newRoot.parentNode = 0;

		    	file.writeNode(newRoot);
		    }
			return;
		}

		// adjust the bounding boxes in the parents for Node n1
		NoneLeafNode p = (NoneLeafNode) n1.getParent();
		p.hyperBBs[n1.place] = n1.getUnionMinBB();
		p.unionMinBB = (p.getUnionMinBB()).unionBoundingBox(n1.getUnionMinBB());


		file.writeNode(p);

		// propagate adjustment upwards
		if(n2 == null) {
			adjustTree(p, null);
		}
		else {
			// as there occured a split - the second node has to be inserted
			Node[] newNodes = new Node[] {null, null};
			if(p.getUsedSpace() < (file.getCapacity()-1)) {
				// new split must happen
				p.insertData( n2, n2.getUnionMinBB());
				file.writeNode(p);
				newNodes[0] = p;
			}
			else {
				p.insertData( n2, n2.getUnionMinBB());
				file.writeNode(p);
				newNodes = splitNode(p);
			}
			adjustTree(newNodes[0], newNodes[1]);
		}
	}


	private class ABL implements Comparable {

		Node node;
		double minDist;

		public ABL(Node node, double minDist) {
			this.node = node;
			this.minDist = minDist;
		}

		public int compareTo(Object obj) {
			ABL help = (ABL)obj;
			if(this.minDist < help.minDist)
				return -1;
			else {
				if(this.minDist > help.minDist)
					return 1;
				else
					return 0;
			}
		}
	}


//	--------------------------------------------------------------------------

	/**
	 * retrieves all entries which are inside of the box
	 * @param box the hyperboundingbox from which nodes are to be retiieved
	 * @return array of POIs in form of HyperboboundingBox
	 * @throws RTreeException
	 */
	public Vector<HyperBoundingBox> retrieve(HyperBoundingBox box) throws RTreeException {
		if(box.getDimension() != file.getDimension())
			throw new IllegalArgumentException("HyperBoundingBox has wrong dimension");
		
		Vector<HyperBoundingBox> v = new Vector<>();
		// ruft die eigentliche suche auf
		try {
			walk(file.readNode(0), v, box);
		} catch(PageFileException e) {
			e.fillInStackTrace();
			throw new RTreeException("PageFileException RTree.search() - readNode()");			
		}
		return v;
	}
	

	private void walk(Node node1, Vector<HyperBoundingBox> v, HyperBoundingBox box) {
		//System.out.println("Entered");
		if(node1 instanceof LeafNode) {			
			LeafNode node = (LeafNode) node1;
			//System.out.println("Leafnode");
			for(int i=0; i < node.getUsedSpace(); i++) {
				// wenn eintr�ge enthalten diese in Vechtor aufnehmen;
				if(node.hyperBBs[i].equals(box) || box.contains(node.hyperBBs[i])) {
					v.addElement(node.getHyperBoundingBox(i));
					System.out.println(node.hyperBBs[i] +  " Data: " + node.data[i]);
				}
			}			
			return;
		}
		else {					
			NoneLeafNode node = (NoneLeafNode) node1;
			//System.out.println("NonLeafNOde");
			// node ist kein LeafNode
			// alle eintra�ge auf �berlappung durchsuchen
			//System.out.println(node.getUsedSpace());
			for(int i=0; i < node.getUsedSpace(); i++) {
				// wenn enthalten rekursiv search mit diesem node aufrufen
				//if(node.hyperBBs[i].contains(box)) {
				//System.out.println(box.contains(node.hyperBBs[i]));
				if(box.contains(node.hyperBBs[i]) || box.overlaps(node.hyperBBs[i])) {
					walk((Node)node.getData(i), v, box);
				}
			}				
		}
	}


	/**
	 * retrieves all entries which are inside of the ellipse
	 * @param ellipse the Ellipse from which nodes are to be retiieved
	 * @return array of POIs in form of HyperboboundingBox
	 * @throws RTreeException
	 */
	public Vector<HyperBoundingBox> rangeQuery(Ellipse ellipse) throws RTreeException {
//		if(box.getDimension() != file.getDimension())
//			throw new IllegalArgumentException("HyperBoundingBox has wrong dimension");

		Vector<HyperBoundingBox> v = new Vector<>();
		// ruft die eigentliche suche auf
		try {
			walkEllipse(file.readNode(0), v, ellipse);
		} catch(PageFileException e) {
			e.fillInStackTrace();
			throw new RTreeException("PageFileException RTree.search() - readNode()");
		}
		return v;
	}


	private void walkEllipse(Node node1, Vector<HyperBoundingBox> v, Ellipse ellipse) {

		if(node1 instanceof LeafNode) {
			LeafNode node = (LeafNode) node1;

			for(int i=0; i < node.getUsedSpace(); i++) {

				HyperBoundingBox box = node.hyperBBs[i];
				if(ellipse.inside(box.getPMin())){
					v.addElement(node.getHyperBoundingBox(i));
					System.out.println(node.hyperBBs[i] +  " Data: " + node.data[i]);
				}

			}
			return;
		}
		else {
			NoneLeafNode node = (NoneLeafNode) node1;

			for(int i=0; i < node.getUsedSpace(); i++) {

				//System.out.println(box.contains(node.hyperBBs[i]));
				HyperBoundingBox box = node.hyperBBs[i];
				if(ellipse.intersect(box) || ellipse.inside(box)){
					walkEllipse((Node)node.getData(i), v, ellipse);
				}
			}
		}
	}


	/**
	 * Queries the nearest neighbour to given search HyperPoint
	 * @param point - search point
	 * @return HyperboboundingBox - the nearest POI
	 */
	public HyperBoundingBox nearestNeighbour2(HyperPoint point) throws RTreeException {
		int dim = point.getDimension();
		double x[] = new double[dim];
		double a = Math.sqrt(Double.POSITIVE_INFINITY);
		for(int i=0;i<dim;i++){
			x[i] = a;
		}
		try {
			HyperPoint pMin = new HyperPoint(x);
			HyperBoundingBox box = new HyperBoundingBox(pMin, pMin);
			return nearestNeighbour2(file.readNode(0), point, box);
		} catch(PageFileException e) {
			e.fillInStackTrace();
			throw new RTreeException("PageFileException - nearestNeighbour - readNode(0)");
		}
	}

	// private method for nearest neighbour query
	private HyperBoundingBox nearestNeighbour2(Node node, HyperPoint point, HyperBoundingBox temp) {


		if(node instanceof LeafNode) {
			// if mindist this < tempDist
			for(int i = 0; i < node.getUsedSpace(); i++) {
				double dist = node.getHyperBoundingBox(i).minDist(point);
				if(dist < temp.minDist(point)) {
					// then this = nearest Neighbour - update tempDist
					//temp[1] = ((LeafNode)node).data[i];
					//temp[0] = dist;
					temp = node.getHyperBoundingBox(i);
					temp.setData(((LeafNode)node).data[i]);
				}
			}

		}
		else {
			// inner class ABL

			// generate ActiveBranchList of node
			ABL[] abl = new ABL[node.getUsedSpace()];
			for(int i=0; i < node.getUsedSpace(); i++) {
				Node help = (Node)node.getData(i);
				abl[i] = new ABL(help, help.getUnionMinBB().minDist(point));
			}

			//sort activebranchlist
			Arrays.sort(abl);

			for(int i=0; i < abl.length; i++) {
				// apply heuristic 3
				if(abl[i].minDist <= temp.minDist(point)) {
					temp = nearestNeighbour2(abl[i].node, point, temp);
				}
			}
		}
		return temp;
	}


	/**
	 * Queries the nearest neighbour to given search HyperPoint inside a given HyperBox
	 * @param point - search point
	 * @param searchRegion - the hyperboundingbox which is to be queried
	 * @return HyperboboundingBox - the nearest POI
	 */
	public HyperBoundingBox boundedNNSearch(HyperPoint point,HyperBoundingBox searchRegion) throws RTreeException {
		try {
			HyperPoint max = searchRegion.getPMax();
			double l[] = new double[max.getDimension()];
			for(int i=0;i<max.getDimension();i++){
				if(max.getCoord(i) > 0)
					l[i] = Math.sqrt(Double.POSITIVE_INFINITY);
				else
					l[i] = Math.sqrt(Double.POSITIVE_INFINITY) * (-1);
			}
			//double a = Math.sqrt(Double.POSITIVE_INFINITY);
			HyperPoint pMin = new HyperPoint(l);
			HyperBoundingBox box = new HyperBoundingBox(pMin, pMin);

			HyperBoundingBox temp = boundedNNSearch(file.readNode(0), searchRegion, point, box);
//            if(!searchRegion.contains(temp))
//                return null;
			if(box.equals(temp))
				return null;
			else return temp;
		} catch(PageFileException e) {
			e.fillInStackTrace();
			throw new RTreeException("PageFileException - nearestNeighbour - readNode(0)");
		}
	}

	// private method for nearest neighbour query
	private HyperBoundingBox boundedNNSearch(Node node,HyperBoundingBox searchRegion, HyperPoint point, HyperBoundingBox temp) {


		if(node instanceof LeafNode) {
			// if mindist this < tempDist
			for(int i = 0; i < node.getUsedSpace(); i++) {
				HyperBoundingBox box = node.getHyperBoundingBox(i);
				double dist = node.getHyperBoundingBox(i).minDist(point);
				if(dist < temp.minDist(point)  && searchRegion.contains(box)) {
					// then this = nearest Neighbour - update tempDist
					//System.out.println(temp + " " + searchRegion +" " + searchRegion.contains(box));
					temp = box;
					temp.setData(((LeafNode)node).data[i]);
				}
			}

		}
		else {
			// inner class ABL

			// generate ActiveBranchList of node
			Vector <ABL> abl = new Vector<>();
			//ABL[] abl = new ABL[node.getUsedSpace()];
			for(int i=0; i < node.getUsedSpace(); i++) {
				HyperBoundingBox box = node.getHyperBoundingBox(i);
				if(box.overlaps(searchRegion) || box.contains(searchRegion) || searchRegion.contains(box)) {
					Node help = (Node) node.getData(i);
					abl.add(new ABL(help, help.getUnionMinBB().minDist(point)));
				}
			}

			abl.sort(new Comparator<ABL>() {
				@Override
				public int compare(ABL o1, ABL o2) {
					if(o1.minDist < o2.minDist)
						return -1;
					else {
						if(o1.minDist > o2.minDist)
							return 1;
						else
							return 0;
					}
				}
			});
			//sort activebranchlist
			//Arrays.sort(abl);

			for(int i=0; i < abl.size(); i++) {
				// apply heuristic 3
				if(abl.elementAt(i).minDist <= temp.minDist(point)) {
					temp = boundedNNSearch(abl.elementAt(i).node, searchRegion, point, temp);
				}
			}
		}
		return temp;
	}
}
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
 * Point in multidimensional space.
 * Based on double coordinates.
 * </p>
 * @author Wolfgang Baer - WBaer@gmx.de
 */
public class HyperPoint {
	
	private double[] coords;
	
	/**
	 * Constructor.<br>
	 * Length of given array sets the dimension.
	 * @param coords - array with double values of the coordinates.
	 */
	public HyperPoint(double[] coords) {
		this.coords = coords;
	}
	
	/**
	 * Creates a null HyperPoint with coordinates Double.NaN.
	 * Mostly used internal.
	 * @param dimension - int for dimension of point
	 * @return HyperPoint
	 */
	protected static HyperPoint getNullHyperPoint(int dimension) {
		double[] point = new double[dimension];
		for(int i=0; i < dimension; i++)
			point[i] = Double.NaN;
		return new HyperPoint(point);
	}
	
		
	/**
	 * Returns the coordinates as double array.
	 * @return double[]
	 */
	public double[] getCoords() {
		return coords;
	}
	
	/** 
	 * Returns the coordinate for given index.
	 * @param index - int
	 * @return double -  coordinate
	 */
	public double getCoord(int index) {
		return coords[index];
	}
	
	/**
	 * Returns the dimension of the HyperPoint.
	 * @return int 
	 */
	public int getDimension() {
		return coords.length ;
	}
	
	/**
	 * Builds a String representation of the HyperPoint.
	 * @return String 
	 */	
	public String toString() {
		String ret = "";
		for(int i=0; i < coords.length; i++) {
			ret += coords[i]+", ";
		}
		return ret;
	}
	
	/**
	 * Implements equals
	 * @see Object#equals(Object)
	 */	
	public boolean equals(Object obj) {
		HyperPoint point = (HyperPoint) obj;
		boolean state = true;
		for(int i=0; i < coords.length; i++)
			if(this.coords[i] != point.coords[i])
				state = false;
		return state;		
	}
	
	
	/**
	 * Implements clone.
	 * @see Object#clone()
	 */
	public Object clone() {
		double[] point = new double[coords.length];
		for(int i = 0; i < coords.length; i++)
			point[i] = coords[i];
		
		return new HyperPoint(point);	
	}
}

/*
 * 	This file is part of DicomFlow.
 * 
 * 	DicomFlow is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either versio3 of the License, or
 * 	(at your option) any later version.
 * 
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */


package br.ufpb.dicomflow.util;


public interface PagerIF {
	
	public static final int INITIAL_PAGE = 1;
	public static final int DEFAULT_PAGE_NUMBER = 1;
	public static final int DEFAULT_PAGE_QUANTITY = 25;
	public static final String NEXT_PAGE = "next";
	public static final String BACK_PAGE = "back";
	public static final String TOP_PAGE = "top";
	
	public void pageUpdate(String command);
	
	public int getMax() ;

    public int getFirst();
    
    public void setSize(int newSize);
    
    public boolean isNotFirstPage();
    
    public boolean isNotLastPage();
    
}

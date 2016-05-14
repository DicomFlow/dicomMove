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



public class Pager implements PagerIF {
	
	private int pageNumber;
	
	
    private int pageQuantity;
    private int quantity;
    
    //comentário
	
    
    private Pager(int pageNumber, int pageQuantity, int quantity){
    	this.pageNumber = pageNumber;
    	this.pageQuantity = pageQuantity;
    	this.quantity = quantity;
    }
    
    
    private Pager() {
    	this.pageNumber = DEFAULT_PAGE_NUMBER;
    	this.pageQuantity = DEFAULT_PAGE_QUANTITY;
	}

	/**
     * @return
     */
    public int getMax() {
        return pageQuantity;
    }

    /**
     * @return
     */
    public int getFirst() {
        return (pageNumber * pageQuantity) - pageQuantity;
    }
    
    public void nextPageAction(){
        pageNumber++;
        
    }
    
    public void backPageAction(){
        pageNumber--;
    }
    
    public void topPageAction(){
        pageNumber = INITIAL_PAGE;
    }
    
    public boolean isNotFirstPage(){
        return !(pageNumber == INITIAL_PAGE); 
    }
    
    
    public boolean isNotLastPage(){
        return !(quantity < pageQuantity);
    }
    
    public boolean isCollectionEmpty(){
        return quantity == 0;
    }
    
	public void pageUpdate(String command){
    	if(command != null && command.equals(NEXT_PAGE)){
    		nextPageAction();
    	}else if(command != null &&  command.equals(BACK_PAGE)){
    		backPageAction();
    	}else if(command != null &&  command.equals(TOP_PAGE)) {
    		topPageAction();
    	}else{
    		topPageAction();
    	}
    }
    
    public static PagerIF createDefaultPager(){
    	return new Pager();
    }
    
    public static PagerIF createPager(int pageNumber, int pageQuantity, int quantity){
    	return new Pager(pageNumber, pageQuantity, quantity);
    }

	
	public void setSize(int newSize) {
		this.quantity = newSize;
	}
    
}

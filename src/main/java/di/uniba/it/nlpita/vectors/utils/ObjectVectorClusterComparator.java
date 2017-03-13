/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.nlpita.vectors.utils;


import di.uniba.it.nlpita.vectors.ObjectVector;
import java.util.Comparator;

/**
 *
 * @author pierpaolo
 */
public class ObjectVectorClusterComparator implements Comparator<ObjectVector> {

    @Override
    public int compare(ObjectVector o1, ObjectVector o2) {
        return Integer.compare(o1.getCluster(), o2.getCluster());
    }
    
}

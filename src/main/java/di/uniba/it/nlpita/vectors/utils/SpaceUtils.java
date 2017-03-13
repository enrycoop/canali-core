/**
 * Copyright (c) 2014, the Temporal Random Indexing AUTHORS.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Bari nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * GNU GENERAL PUBLIC LICENSE - Version 3, 29 June 2007
 *
 */
package di.uniba.it.nlpita.vectors.utils;

import di.uniba.it.nlpita.vectors.ObjectVector;
import di.uniba.it.nlpita.vectors.RealVector;
import di.uniba.it.nlpita.vectors.ReverseObjectVectorComparator;
import di.uniba.it.nlpita.vectors.Vector;
import di.uniba.it.nlpita.vectors.VectorFactory;
import di.uniba.it.nlpita.vectors.VectorReader;
import di.uniba.it.nlpita.vectors.VectorType;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Random;

/**
 *
 * @author pierpaolo
 */
public class SpaceUtils {

    public static List<ObjectVector> getNearestVectors(VectorReader store, String word, int n) throws IOException {
        Vector vector = store.getVector(word);
        if (vector != null) {
            return getNearestVectors(store, vector, n);
        } else {
            return new ArrayList<>();
        }
    }

    public static List<ObjectVector> getNearestVectors(VectorReader store, Vector vector, int n) throws IOException {
        PriorityQueue<ObjectVector> queue = new PriorityQueue<>();
        Iterator<ObjectVector> allVectors = store.getAllVectors();
        while (allVectors.hasNext()) {
            ObjectVector ov = allVectors.next();
            if (!ov.getVector().isZeroVector()) {
                double overlap = ov.getVector().measureOverlap(vector);
                ov.setScore(overlap);
                if (queue.size() <= n) {
                    queue.offer(ov);
                } else {
                    queue.poll();
                    queue.offer(ov);
                }
            }
        }
        queue.poll();
        List<ObjectVector> list = new ArrayList<>(queue);
        Collections.sort(list, new ReverseObjectVectorComparator());
        return list;
    }

    public static List<ObjectVector> getNearestVectors(VectorReader store, String word, double tr) throws IOException {
        Vector vector = store.getVector(word);
        if (vector != null) {
            return getNearestVectors(store, vector, tr);
        } else {
            return new ArrayList<>();
        }
    }

    public static List<ObjectVector> getNearestVectors(VectorReader store, Vector vector, double tr) throws IOException {
        List<ObjectVector> rs = new ArrayList<>();
        Iterator<ObjectVector> allVectors = store.getAllVectors();
        while (allVectors.hasNext()) {
            ObjectVector ov = allVectors.next();
            if (!ov.getVector().isZeroVector()) {
                double overlap = ov.getVector().measureOverlap(vector);
                if (overlap >= tr) {
                    ov.setScore(overlap);
                    rs.add(ov);
                }
            }
        }
        Collections.sort(rs, new ReverseObjectVectorComparator());
        return rs;
    }

    public static int countVectors(VectorReader reader) throws IOException {
        Iterator<ObjectVector> allVectors = reader.getAllVectors();
        int counter = 0;
        while (allVectors.hasNext()) {
            allVectors.next();
            counter++;
        }
        return counter;

    }

    public static List<ObjectVector> analogy(VectorReader reader, Vector v1, Vector v2, Vector v3, int n) throws IOException {
        Vector m1 = v1.copy();
        m1.superpose(v3, -1, null);
        PriorityQueue<ObjectVector> queue = new PriorityQueue<>();
        Iterator<ObjectVector> allVectors = reader.getAllVectors();
        while (allVectors.hasNext()) {
            ObjectVector ov = allVectors.next();
            Vector m2 = v2.copy();
            m2.superpose(ov.getVector(), -1, null);
            ov.setScore(m1.measureOverlap(m2));
            if (queue.size() <= n) {
                queue.offer(ov);
            } else {
                queue.poll();
                queue.offer(ov);
            }
        }
        queue.poll();
        List<ObjectVector> list = new ArrayList<>(queue);
        Collections.sort(list, new ReverseObjectVectorComparator());
        return list;
    }

    public static int getNearestVector(Vector v, Vector[] vectors) {
        if (vectors.length > 0) {
            int j = 0;
            double max = vectors[0].measureOverlap(v);
            for (int i = 1; i < vectors.length; i++) {
                double sim = vectors[i].measureOverlap(v);
                if (sim > max) {
                    max = sim;
                    j = i;
                }
            }
            return j;
        } else {
            return -1;
        }
    }

    public static Clusters kMeansCluster(VectorReader vr, ObjectVector[] objectVectors, int k) throws IOException {
        Clusters clusters = new Clusters(new int[objectVectors.length], new Vector[k]);
        Random rand = new Random();

        // Initialize cluster mappings randomly.
        for (int i = 0; i < objectVectors.length; ++i) {
            int randInt = rand.nextInt(Integer.MAX_VALUE);
            clusters.getClusterMappings()[i] = randInt % k;
        }

        // Loop that computes centroids and reassigns members.
        boolean clustering = true;
        while (clustering) {
            // Clear centroid register.
            for (int i = 0; i < clusters.getCentroids().length; ++i) {
                clusters.getCentroids()[i] = VectorFactory.createZeroVector(VectorType.REAL, vr.getDimension());
            }
            // Generate new cluster centroids.
            for (int i = 0; i < objectVectors.length; ++i) {
                clusters.getCentroids()[clusters.getClusterMappings()[i]].superpose(objectVectors[i].getVector(), 1, null);
            }
            for (int i = 0; i < k; ++i) {
                clusters.getCentroids()[i].normalize();
            }

            boolean changeFlag = false;
            // Map items to clusters.
            for (int i = 0; i < objectVectors.length; i++) {
                int j = getNearestVector(objectVectors[i].getVector(), clusters.getCentroids());
                if (j != clusters.getClusterMappings()[i]) {
                    changeFlag = true;
                    clusters.getClusterMappings()[i] = j;
                }
            }
            if (changeFlag == false) {
                clustering = false;
            }
        }

        return clusters;
    }

    public static Vector getMean(VectorReader vr) throws IOException {
        Vector vector = VectorFactory.createZeroVector(VectorType.REAL, vr.getDimension());
        Iterator<ObjectVector> it = vr.getAllVectors();
        float d = 0;
        while (it.hasNext()) {
            ObjectVector ov = it.next();
            vector.superpose(ov.getVector(), 1, null);
            d++;
        }
        float[] cv = ((RealVector) vector).getCoordinates();
        for (int i = 0; i < cv.length; i++) {
            cv[i] = cv[i] / d;
        }
        //vector.normalize();
        return vector;
    }

}

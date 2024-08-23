package org.test.mygpg;

public class PGPAlgorithms {

    public final int hashAlgorithmTags;
    public final int symmetricKeyAlgorithmTags;
    public final int compressionAlgorithmTags;


    public PGPAlgorithms(int hashAlgorithmTags,int symmetricKeyAlgorithmTags,int compressionAlgorithmTags){
        this.hashAlgorithmTags = hashAlgorithmTags;
        this.symmetricKeyAlgorithmTags = symmetricKeyAlgorithmTags;
        this.compressionAlgorithmTags = compressionAlgorithmTags;
    }


}

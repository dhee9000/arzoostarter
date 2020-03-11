package com.codestream.arzoo;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.concurrent.CompletableFuture;

public class Animal {

    private static final String TAG = "ANIMAL";

    private static final String imageName = "Animal.png";
    private static final String modelURI = "models/Zebra.sfb";
    private final String name = "Animal";
    public CompletableFuture<ModelRenderable> model;

    public Animal(Context context){
        if(this.model == null){
            this.model = ModelRenderable.builder()
                        .setSource(context, Uri.parse(modelURI))
                        .build();
        }
    }

    public String getImageName(){
        return imageName;
    }

    public String getName(){ return name; }

    public CompletableFuture<ModelRenderable> getModel(){
        return this.model;
    }
}

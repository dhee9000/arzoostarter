package com.codestream.arzoo;

import android.content.Context;
import android.net.Uri;

import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.concurrent.CompletableFuture;

public class Zebra extends Animal {

    private static final String TAG = "ANIMAL";

    private static final String imageName = "Zebra.png";
    private static final String modelURI = "models/Zebra.sfb";
    public CompletableFuture<ModelRenderable> model;

    private final String name = "Zebra";

    public Zebra(Context context){
        super(context);
        this.model = ModelRenderable.builder()
                .setSource(context, Uri.parse(modelURI))
                .build();
    }
}

package com.example.app;

import android.Manifest;
import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.util.Rotation;
import java.io.File;

public class MainActivity extends Activity {
    GPUImageView v; Camera c;
    String gb="precision lowp float;varying vec2 textureCoordinate;uniform sampler2D inputImageTexture;void main(){vec2 uv=floor(textureCoordinate*160.0)/160.0;vec4 c=texture2D(inputImageTexture,uv);float l=dot(c.rgb,vec3(0.299,0.587,0.114));float d=mod(floor(gl_FragCoord.x)+floor(gl_FragCoord.y),2.0)*0.04;l+=d-0.02;vec3 col=vec3(0.05,0.18,0.05);if(l>0.75)col=vec3(0.6,0.68,0.1);else if(l>0.5)col=vec3(0.38,0.48,0.1);else if(l>0.25)col=vec3(0.14,0.32,0.1);gl_FragColor=vec4(col,1.0);}";
    String pol="precision lowp float;varying vec2 textureCoordinate;uniform sampler2D inputImageTexture;void main(){vec4 c=texture2D(inputImageTexture,textureCoordinate);c.r=c.r*1.15+0.02;c.g=c.g*0.95+0.02;c.b=c.b*0.82+0.08;float vt=1.0-distance(textureCoordinate,vec2(0.5))*0.55;gl_FragColor=vec4(c.rgb*vt,1.0);}";
    String fuji="precision lowp float;varying vec2 textureCoordinate;uniform sampler2D inputImageTexture;float rng(vec2 co){return fract(sin(dot(co,vec2(12.9898,78.233)))*43758.5453);}void main(){vec4 c=texture2D(inputImageTexture,textureCoordinate);c.r*=0.88;c.g*=1.02;c.b*=1.08;float n=(rng(textureCoordinate+gl_FragCoord.xy)-0.5)*0.07;gl_FragColor=vec4(c.rgb+n,1.0);}";

    @Override protected void onCreate(Bundle b){
        super.onCreate(b); setContentView(R.layout.activity_main);
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        v = findViewById(R.id.view);
        
        int w = getResources().getDisplayMetrics().widthPixels;
        v.getLayoutParams().width = w; v.getLayoutParams().height = w;
        v.setRotation(Rotation.ROTATION_90);
        
        findViewById(R.id.b_gb).setOnClickListener(x -> v.setFilter(new GPUImageFilter(GPUImageFilter.NO_FILTER_VERTEX_SHADER, gb)));
        findViewById(R.id.b_pol).setOnClickListener(x -> v.setFilter(new GPUImageFilter(GPUImageFilter.NO_FILTER_VERTEX_SHADER, pol)));
        findViewById(R.id.b_fuji).setOnClickListener(x -> v.setFilter(new GPUImageFilter(GPUImageFilter.NO_FILTER_VERTEX_SHADER, fuji)));
        
        findViewById(R.id.shutter).setOnClickListener(x -> {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            v.saveToPictures(new File(path, "Cambro").getAbsolutePath(), "IMG_" + System.currentTimeMillis() + ".jpg", null);
        });
    }
    @Override protected void onResume(){ super.onResume(); try{ c = Camera.open(); v.setUpCamera(c); }catch(Exception e){} }
    @Override protected void onPause(){ super.onPause(); if(c!=null){ c.release(); c=null; } }
}
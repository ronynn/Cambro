package com.example.app;
import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class MainActivity extends Activity {
    GPUImageView v; Camera c; int i=0;
    String gb="precision lowp float;varying vec2 textureCoordinate;uniform sampler2D inputImageTexture;void main(){vec4 c=texture2D(inputImageTexture,floor(textureCoordinate*120.0)/120.0);float l=floor(dot(c.rgb,vec3(0.29,0.58,0.11))*3.0)/3.0;gl_FragColor=vec4(mix(vec3(0.05,0.2,0.05),vec3(0.6,0.7,0.1),l),1.0);}";
    String pol="precision lowp float;varying vec2 textureCoordinate;uniform sampler2D inputImageTexture;void main(){vec4 c=texture2D(inputImageTexture,textureCoordinate);c.r=c.r*1.2+0.1;c.b*=0.9;c.rgb-=distance(textureCoordinate,vec2(0.5))*0.4;gl_FragColor=c;}";
    String fuji="precision lowp float;varying vec2 textureCoordinate;uniform sampler2D inputImageTexture;float r(vec2 c){return fract(sin(dot(c,vec2(12.98,78.23)))*43758.5);}void main(){vec4 c=texture2D(inputImageTexture,textureCoordinate);c.r*=0.9;c.b=c.b*1.2+0.05;c.rgb+=(r(textureCoordinate)-0.5)*0.15;gl_FragColor=c;}";
    GPUImageFilter[] f;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);setContentView(R.layout.activity_main);
        requestPermissions(new String[]{Manifest.permission.CAMERA},1);
        f=new GPUImageFilter[]{new GPUImageFilter(GPUImageFilter.NO_FILTER_VERTEX_SHADER,gb),new GPUImageFilter(GPUImageFilter.NO_FILTER_VERTEX_SHADER,pol),new GPUImageFilter(GPUImageFilter.NO_FILTER_VERTEX_SHADER,fuji)};
        v=findViewById(R.id.view);v.setFilter(f[0]);
        findViewById(R.id.btn).setOnClickListener(x->v.setFilter(f[i=(i+1)%3]));
    }
    @Override protected void onResume(){super.onResume();try{v.setUpCamera(c=Camera.open());}catch(Exception e){}}
    @Override protected void onPause(){super.onPause();if(c!=null){c.release();c=null;}}
}
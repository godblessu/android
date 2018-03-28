package com.tym.shortvideo.filter.advanced;

import android.opengl.GLES20;

import com.tym.video.R;
import com.tym.shortvideo.filter.base.GPUImageFilter;
import com.tym.shortvideo.filter.helper.OpenGlUtils;
import com.tym.shortvideo.filter.helper.type.GlUtil;
import com.tym.shortvideo.recodrender.ParamsManager;
public class MagicRiseFilter extends GPUImageFilter {
	private int[] inputTextureHandles = {-1,-1,-1};
	private int[] inputTextureUniformLocations = {-1,-1,-1};
	
	public MagicRiseFilter(){
		super(R.raw.rise);
	}
	
	@Override
	public void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(inputTextureHandles.length, inputTextureHandles, 0);
        for(int i = 0; i < inputTextureHandles.length; i++) {
			inputTextureHandles[i] = -1;
		}
    }
	
	@Override
	protected void onDrawArraysAfter(){
		for(int i = 0; i < inputTextureHandles.length
				&& inputTextureHandles[i] != OpenGlUtils.NO_TEXTURE; i++){
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3));
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		}
	}
	  
	@Override
	protected void onDrawArraysPre(){
		for(int i = 0; i < inputTextureHandles.length 
				&& inputTextureHandles[i] != OpenGlUtils.NO_TEXTURE; i++){
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3) );
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureHandles[i]);
			GLES20.glUniform1i(inputTextureUniformLocations[i], (i+3));
		}
	}
	
	@Override
	public void onInit(){
		super.onInit();
		for(int i=0; i < inputTextureUniformLocations.length; i++){
			inputTextureUniformLocations[i] = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture"+(2+i));
		}
	}
	
	@Override
	public void onInitialized(){
		super.onInitialized();
	    runOnDraw(new Runnable(){
		    @Override
			public void run(){
		    	inputTextureHandles[0] = GlUtil.createTextureFromAssets(ParamsManager.context, "filter/blackboard1024.png");
				inputTextureHandles[1] = GlUtil.createTextureFromAssets(ParamsManager.context, "filter/overlaymap.png");
				inputTextureHandles[2] = GlUtil.createTextureFromAssets(ParamsManager.context, "filter/risemap.png");
		    }
	    });
	}
}
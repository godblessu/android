package com.tym.shortvideo.filter.base.avfilter;

import android.content.res.Resources;
import android.opengl.GLES30;
import android.util.Log;
import android.util.SparseArray;


import com.tym.shortvideo.utils.CameraUtils;
import com.tym.shortvideo.utils.MatrixUtils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;


public abstract class AFilter {

    private static final String TAG="Filter";


    public static boolean DEBUG=true;
    /**
     * 单位矩阵
     */
    public static final float[] OM= MatrixUtils.getOriginalMatrix();
    /**
     * 程序句柄
     */
    protected int mProgram;
    /**
     * 顶点坐标句柄
     */
    protected int mHPosition;
    /**
     * 纹理坐标句柄
     */
    protected int mHCoord;
    /**
     * 总变换矩阵句柄
     */
    protected int mHMatrix;
    /**
     * 默认纹理贴图句柄
     */
    protected int mHTexture;

    protected Resources mRes;


    /**
     * 顶点坐标Buffer
     */
    protected FloatBuffer mVerBuffer;

    /**
     * 纹理坐标Buffer
     */
    protected FloatBuffer mTexBuffer;

    /**
     * 索引坐标Buffer
     */

    protected int mFlag=0;

    private float[] matrix= Arrays.copyOf(OM,16);

    private int textureType=0;      //默认使用Texture2D0
    private int textureId=0;
    //顶点坐标
    protected float pos[] = {
            -1.0f,  1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f,  -1.0f,
    };

    //纹理坐标
    protected float[] coord={

//            0.002f, 0.0f,
//            0.002f, 1.0f,
//            1.0f, 0.0f,
//            1.0f, 1.0f,


            0.002f, 0.0f,
            0.002f, 0.998f,
            1.0f, 0.0f,
            1.0f, 0.998f,
    };

    private SparseArray<boolean[]> mBools;
    private SparseArray<int[]> mInts;
    private SparseArray<float[]> mFloats;

    public AFilter(Resources mRes){
        this.mRes=mRes;
        initBuffer();
    }

    public final void create(){
        onCreate();
    }

    public final void setSize(int width,int height){
        onSizeChanged(width,height);
    }

    public void draw(){
        onClear();
        onUseProgram();
        onSetExpandData();
        onBindTexture();
        onDraw();
    }

    public final void setMatrix(float[] matrix){
        this.matrix=matrix;
    }

    public float[] getMatrix(){
        return matrix;
    }

    public final void setTextureType(int type){
        this.textureType=type;
    }

    public final int getTextureType(){
        return textureType;
    }

    public final int getTextureId(){
        return textureId;
    }

    public final void setTextureId(int textureId){
        this.textureId=textureId;
    }

    public void setFlag(int flag){
        this.mFlag=flag;
    }

    public int getFlag(){
        return mFlag;
    }

    public void setFloat(int type,float ... params){
        if(mFloats==null){
            mFloats=new SparseArray<>();
        }
        mFloats.put(type,params);
    }
    public void setInt(int type,int ... params){
        if(mInts==null){
            mInts=new SparseArray<>();
        }
        mInts.put(type,params);
    }
    public void setBool(int type,boolean ... params){
        if(mBools==null){
            mBools=new SparseArray<>();
        }
        mBools.put(type,params);
    }

    public boolean getBool(int type,int index) {
        if (mBools == null) {
            return false;
        }
        boolean[] b = mBools.get(type);
        return !(b == null || b.length <= index) && b[index];
    }

    public int getInt(int type,int index){
        if (mInts == null) {
            return 0;
        }
        int[] b = mInts.get(type);
        if(b == null || b.length <= index){
            return 0;
        }
        return b[index];
    }

    public float getFloat(int type,int index){
        if (mFloats == null) {
            return 0;
        }
        float[] b = mFloats.get(type);
        if(b == null || b.length <= index){
            return 0;
        }
        return b[index];
    }

    public int getOutputTexture(){
        return -1;
    }

    /**
     * 实现此方法，完成程序的创建，可直接调用createProgram来实现
     */
    protected abstract void onCreate();
    protected abstract void onSizeChanged(int width,int height);

    protected final void createProgram(String vertex, String fragment){
        mProgram= uCreateGlProgram(vertex,fragment);
        mHPosition= GLES30.glGetAttribLocation(mProgram, "vPosition");
        mHCoord= GLES30.glGetAttribLocation(mProgram,"vCoord");
        mHMatrix= GLES30.glGetUniformLocation(mProgram,"vMatrix");
        mHTexture= GLES30.glGetUniformLocation(mProgram,"vTexture");
    }

    protected final void createProgramByAssetsFile(String vertex, String fragment){
        createProgram(uRes(mRes,vertex),uRes(mRes,fragment));
    }

    /**
     * Buffer初始化
     */
    protected void initBuffer(){
        ByteBuffer a= ByteBuffer.allocateDirect(32);
        a.order(ByteOrder.nativeOrder());
        mVerBuffer=a.asFloatBuffer();
        mVerBuffer.put(pos);
        mVerBuffer.position(0);
        ByteBuffer b= ByteBuffer.allocateDirect(32);
        b.order(ByteOrder.nativeOrder());
        mTexBuffer=b.asFloatBuffer();
        mTexBuffer.put(coord);
        mTexBuffer.position(0);
    }

    protected void onUseProgram(){
        GLES30.glUseProgram(mProgram);
    }

    /**
     * 启用顶点坐标和纹理坐标进行绘制
     */
    protected void onDraw(){
        GLES30.glEnableVertexAttribArray(mHPosition);
        GLES30.glVertexAttribPointer(mHPosition,2, GLES30.GL_FLOAT, false, 0,mVerBuffer);
        GLES30.glEnableVertexAttribArray(mHCoord);
        GLES30.glVertexAttribPointer(mHCoord, 2, GLES30.GL_FLOAT, false, 0, mTexBuffer);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,4);
        GLES30.glDisableVertexAttribArray(mHPosition);
        GLES30.glDisableVertexAttribArray(mHCoord);
    }

    /**
     * 清除画布
     */
    protected void onClear(){
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * 设置其他扩展数据
     */
    protected void onSetExpandData(){
        GLES30.glUniformMatrix4fv(mHMatrix,1,false,matrix,0);
    }

    /**
     * 绑定默认纹理
     */
    protected void onBindTexture(){
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0+textureType);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,getTextureId());
        GLES30.glUniform1i(mHTexture,textureType);
    }

    public static void glError(int code,Object index){
        if(DEBUG&&code!=0){
            Log.e(TAG,"glError:"+code+"---"+index);
        }
    }

    //通过路径加载Assets中的文本内容
    public static String uRes(Resources mRes, String path){
        StringBuilder result=new StringBuilder();
        try{
            InputStream is=mRes.getAssets().open(path);
            int ch;
            byte[] buffer=new byte[1024];
            while (-1!=(ch=is.read(buffer))){
                result.append(new String(buffer,0,ch));
            }
        }catch (Exception e){
            return null;
        }
        return result.toString().replaceAll("\\r\\n","\n");
    }

    //创建GL程序
    public static int uCreateGlProgram(String vertexSource, String fragmentSource){
        int vertex=uLoadShader(GLES30.GL_VERTEX_SHADER,vertexSource);
        if(vertex==0) {
            return 0;
        }
        int fragment=uLoadShader(GLES30.GL_FRAGMENT_SHADER,fragmentSource);
        if(fragment==0) {
            return 0;
        }
        int program= GLES30.glCreateProgram();
        if(program!=0){
            GLES30.glAttachShader(program,vertex);
            GLES30.glAttachShader(program,fragment);
            GLES30.glLinkProgram(program);
            int[] linkStatus=new int[1];
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS,linkStatus,0);
            if(linkStatus[0]!= GLES30.GL_TRUE){
                glError(1,"Could not link program:"+ GLES30.glGetProgramInfoLog(program));
                GLES30.glDeleteProgram(program);
                program=0;
            }
        }
        return program;
    }

    /**加载shader*/
    public static int uLoadShader(int shaderType,String source){
        int shader= GLES30.glCreateShader(shaderType);
        if(0!=shader){
            GLES30.glShaderSource(shader,source);
            GLES30.glCompileShader(shader);
            int[] compiled=new int[1];
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS,compiled,0);
            if(compiled[0]==0){
                glError(1,"Could not compile shader:"+shaderType);
                glError(1,"GLES30 Error:"+ GLES30.glGetShaderInfoLog(shader));
                GLES30.glDeleteShader(shader);
                shader=0;
            }
        }
        return shader;
    }
}

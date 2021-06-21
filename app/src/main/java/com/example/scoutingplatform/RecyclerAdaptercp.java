package com.example.scoutingplatform;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecyclerAdaptercp extends RecyclerView.Adapter<Holder> {

    Context context;
    ArrayList<Model> models;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    boolean created = false;

    public RecyclerAdaptercp(Context context, ArrayList<Model> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myrecycleritem, null);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {

        holder.txtCP.setText(models.get(position).getCPID());
        holder.txtDetails.setText(models.get(position).getDetails());
        holder.txtBlock.setText(models.get(position).getBlockName());
        if (models.get(position).getCount() >= models.get(position).getCountneeded()) {
            holder.CPrelativeLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.edgesgreen));
        } else if (models.get(position).getCount() < models.get(position).getCountneeded() && models.get(position).getCount() > 0) {
            holder.CPrelativeLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.edgesblue));
        }
        holder.btnView.setOnClickListener(v -> {
            if (models.get(position).isForceScan() && models.get(position).getBarcode().isEmpty()) {

                Intent intents = new Intent(context, CameraScanning.class);
                intents.putExtra("CapturePoint", models.get(position).getCP());
                context.startActivity(intents);
            } else {
                Intent intentls = new Intent(context, DataPointContainerActivity.class);
                intentls.putExtra("CapturePoint", models.get(position).getCP());
                String barcode ="";
                if(models.get(position).getBarcode() != null){
                    barcode = models.get(position).getBarcode();
                }
                intentls.putExtra("Barcode", barcode);
                context.startActivity(intentls);
            }
        });
    }

    protected void onVisible(boolean create, SurfaceView surf) {
        if (!create) {
            barcodeDetector = new BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.ALL_FORMATS).build();
            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                    if (qrCodes.size() > 0) {
                    }
                }
            });
            cameraSource = new CameraSource.Builder(context, barcodeDetector)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(200, 200)
                    .setAutoFocusEnabled(true).build();

            surf.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    Log.d("surf111", "surfaceChanged: ");
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });
            created = true;
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}

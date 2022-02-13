package novamarket.com;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    String TAG = "MapActivity";

    private FusedLocationProviderClient fusedLocationClient;    // 현재 위치를 가져오기 위한 변수
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private MapView mapView;
    double currentLon;
    double currentLat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Log.d(TAG, "onCreate");

        ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        //FusedLocationProviderClient의 인스턴스를 생성.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar_map);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setTitle("장소공유"); // 툴바 제목 설정

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        mapView.getMapAsync(this);


        TextView textView_shareLoaction = findViewById(R.id.textView_shareLoaction);
        textView_shareLoaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"위도" +naverMap.getCameraPosition().target.latitude);
                Log.d(TAG,"경도" +naverMap.getCameraPosition().target.longitude);
                Double latitude = naverMap.getCameraPosition().target.latitude;
                Double longitude = naverMap.getCameraPosition().target.longitude;
                Intent intent = new Intent();
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        Log.d(TAG, "onLowMemory");
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        mapView.onStop();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }


            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {   // 권한요청이 허용된 경우

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();    // 권한요청이 거절된 경우
                    return;
                }

                // 위도와 경도를 가져온다.
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    currentLat = location.getLatitude();
                                    currentLon = location.getLongitude();
                                    Log.d(TAG,"위도: " + currentLat + " 경도: " + currentLon);

                                    CameraPosition cameraPosition = new CameraPosition(new LatLng(currentLat,currentLon),15);
                                    Log.d(TAG,"위도: " + currentLat + " 경도: " + currentLon);
                                    naverMap.setCameraPosition(cameraPosition);
                                }
                            }
                        });
            }
            else{
                Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();    // 권한요청이 거절된 경우
            }
        }
    }






    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        Log.d(TAG,"onMapReady");



        this.naverMap = naverMap;

//        CameraPosition cameraPosition = new CameraPosition(new LatLng(currentLat,currentLon),15);
//        Log.d(TAG,"위도: " + currentLat + " 경도: " + currentLon);
//        naverMap.setCameraPosition(cameraPosition);


        naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);
        naverMap.setLocationSource(locationSource);

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);






        //위치 오버레이
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);

    }
}
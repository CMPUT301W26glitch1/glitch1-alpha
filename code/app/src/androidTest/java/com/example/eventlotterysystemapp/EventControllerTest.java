package com.example.eventlotterysystemapp;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.eventlotterysystemapp.data.models.EventController;
import com.example.eventlotterysystemapp.data.models.EventController.EventData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class EventControllerTest {

    private EventController controller;
    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        controller = new EventController(context);
    }

    @Test
    public void testGetAllEventObjectsReturnsList() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        final ArrayList<EventData>[] result = new ArrayList[1];

        controller.getAllEventObjects(new EventController.EventCallback() {
            @Override
            public void onSuccess(ArrayList<EventData> events) {
                result[0] = events;
                latch.countDown();
            }

            @Override
            public void onError(String message) {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);

        assertNotNull(result[0]);
    }
}
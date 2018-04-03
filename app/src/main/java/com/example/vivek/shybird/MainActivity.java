package com.example.vivek.shybird;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // Declare a DynamoDBMapper object
    AmazonDynamoDBClient dynamoDBClient;
    DynamoDBMapper dynamoDBMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //AWSMobileClient.getInstance().initialize(this).execute();

        // Instantiate a AmazonDynamoDBMapperClient
        dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        // Initialize Button
        Button createTableButton = findViewById(R.id.createTableButton);
        createTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTestTable();
            }
        });
    }

    void createTestTable() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                createTable();
            }
        }).start();
    }

    void createTable() {
        KeySchemaElement kse = new KeySchemaElement().withAttributeName(
                "userId").withKeyType(KeyType.HASH);
        AttributeDefinition ad = new AttributeDefinition().withAttributeName(
                "userId").withAttributeType(ScalarAttributeType.N);
        ProvisionedThroughput pt = new ProvisionedThroughput()
                .withReadCapacityUnits(10L).withWriteCapacityUnits(5L);

        CreateTableRequest request = new CreateTableRequest()
                .withTableName("TestTable")
                .withKeySchema(kse).withAttributeDefinitions(ad)
                .withProvisionedThroughput(pt);
        try {
            Log.d(TAG, "Sending Create table request");
            dynamoDBClient.createTable(request);
            Log.d(TAG, "Create request response successfully recieved");
            Toast.makeText(this, "Create request response successfully recieved", Toast.LENGTH_LONG).show();
        } catch (AmazonServiceException ex) {
            Log.e(TAG, "Error sending create table request", ex);
            Toast.makeText(this, "Error sending create table request" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

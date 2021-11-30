# GCS Dialogue

## How to start the GCS Dialogue Manager

    # First Step - Start the marytts docker container
    sudo docker run -it -p 59125:59125 synesthesiam/marytts:5.2 --voice dfki-pavoque-neutral-hsmm &
    
    # Second Step - Start the Vonda based Dialogue Manager
    cd ~/dfki/drz_sign_of_life_module/
    sh ./run.sh &

    # Final Step - Start the ros nodes
    roslaunch speech_processing speech_processing.launch

## How to call trigger a GCS Dialogue 

There are two options to start a GCS dialogue. 
1. Using the REST_Endpoint:
    
   Send a POST request to `POST http://127.0.0.1:8086/dialog/gcs`. The message body must contain a valid patient_id.

   ```
   {
   "patient_id" : 1
   }
   ```   
    
2. Using the ROS Service:

   To initialize a dialog with the patient you only have to send a Ros Service Call to the gcs_service. The call then looks like this

    `rosservice call /mlt/gcs_service "patient_id: <id>"`

   After finishing the dialog you will get a gcs.msg as result


        Header header
        int32 patient_id
        
        int8 eyes
        int8 awareness
        int8 motorics
        int8 sum
       
        string notes # e.g. "wasn't able to name current location" or "didn't know current day"
    

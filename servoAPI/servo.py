# External module imports
import RPi.GPIO as GPIO
import time
import dbaccess

import threading

event = threading.Event()
tLock = threading.Lock()
dcBool = False
dc = 8 # duty cycle (0-100) for PWM pin


def thread_function(event):
    global dcBool
    global dc
    while not event.is_set():
        dcChange = dbaccess.dbAccess() # Change duty-cycle
        if dc != dcChange:
            tLock.acquire()
            #pwm.ChangeDutyCycle(dcChange)
            dc = dcChange
            dcBool = True
            tLock.release()
    
    

    

# Pin Definitons:
pwmPin = 18 # Broadcom pin 18 (P1 pin 12)



# Pin Setup:
GPIO.setmode(GPIO.BCM) # Broadcom pin-numbering scheme
GPIO.setup(pwmPin, GPIO.OUT) # PWM pin set as output
pwm = GPIO.PWM(pwmPin, 50)  # Initialize PWM on pwmPin 50Hz frequency

# Initial state for PWM:
pwm.start(dc)

# Create thread
x = threading.Thread(target=thread_function, args=(event,))
x.start() # Start thread

print("Here we go! Press CTRL+C to exit")
try:
    while 1:
        if dcBool:
            tLock.acquire()
            pwm.ChangeDutyCycle(dc)
            print str(dc)
            dcBool = False
            tLock.release()
            
        
except KeyboardInterrupt: # If CTRL+C is pressed, exit cleanly:
    event.set()
    pwm.stop() # stop PWM
    GPIO.cleanup() # cleanup all GPIO

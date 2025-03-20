from flask import Flask, request, jsonify
import sqlite3
import bcrypt
from deep_translator import GoogleTranslator
import speech_recognition as spr
from gtts import gTTS
import os
import re
import subprocess

# Initialize the Flask app
app = Flask(__name__)

# Initialize the database and users table if it doesn't exist
def init_db():
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()
    cursor.execute('''CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        email TEXT UNIQUE,
                        password TEXT)''')
    conn.commit()   
    conn.close()

init_db()

# Function to recognize speech
def recognize_speech(recog, source):
    try:
        recog.adjust_for_ambient_noise(source, duration=0.2)
        audio = recog.listen(source)
        recognized_text = recog.recognize_google(audio)
        return recognized_text.lower()
    except spr.UnknownValueError:
        print("Google Speech Recognition could not understand the audio.")
        return None
    except spr.RequestError as e:
        print(f"Could not request results from Google Speech Recognition service; {e}")
        return None

# User registration route
@app.route('/register', methods=['POST'])
def register_user():
    try:
        data = request.get_json()
        if not data:
            return jsonify({'message': 'Invalid JSON format'}), 400

        email = data.get('email')
        password = data.get('password')

        if not email or '@gmail.com' not in email:
            return jsonify({'message': 'Email must be a Gmail address'}), 400

        if not password or len(password) < 8:
            return jsonify({'message': 'Password must be at least 8 characters'}), 400

        conn = sqlite3.connect('users.db')
        cursor = conn.cursor()
        cursor.execute('SELECT * FROM users WHERE email = ?', (email,))
        user = cursor.fetchone()

        if user:
            conn.close()
            return jsonify({'message': 'User with this email already exists'}), 400

        hashed_password = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt())
        cursor.execute('INSERT INTO users (email, password) VALUES (?, ?)', (email, hashed_password.decode('utf-8')))
        conn.commit()
        conn.close()

        return jsonify({'message': 'User registered successfully'}), 201

    except Exception as e:
        return jsonify({'message': 'An error occurred', 'error': str(e)}), 500

# User login route
@app.route('/login', methods=['POST'])
def login_user():
    try:
        data = request.get_json()
        if not data:
            return jsonify({'message': 'Invalid JSON format'}), 400

        email = data.get('email')
        password = data.get('password')

        if not email or not password:
            return jsonify({'message': 'Email and password are required'}), 400

        conn = sqlite3.connect('users.db')
        cursor = conn.cursor()
        cursor.execute('SELECT * FROM users WHERE email = ?', (email,))
        user = cursor.fetchone()
        conn.close()

        if user and bcrypt.checkpw(password.encode('utf-8'), user[2].encode('utf-8')):
            return jsonify({'message': 'Login successful'}), 200
        else:
            return jsonify({'message': 'Invalid credentials'}), 400

    except Exception as e:
        return jsonify({'message': 'An error occurred', 'error': str(e)}), 500

# Text translation route
@app.route('/translate', methods=['POST'])
def translate_text_route():
    try:
        data = request.get_json()
        if not data:
            return jsonify({'error': 'Invalid JSON format'}), 400

        text = data.get("text")
        source_lang = data.get("source_lang", "auto")  # Auto-detect source language
        target_lang = data.get("target_lang")  # Get from frontend

        if not text or not target_lang:
            return jsonify({'error': 'Missing "text" or "target_lang" field'}), 400

        translated_text = GoogleTranslator(source=source_lang, target=target_lang).translate(text)

        return jsonify({'translated_text': translated_text}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500


# Itinerary generation route
@app.route('/generate_itinerary', methods=['POST'])
def generate_itinerary():
    try:
        data = request.get_json()
        if not data:
            return jsonify({"error": "Invalid JSON data received"}), 400

        budget = data.get("budget")
        destination = data.get("destination")
        num_people = data.get("num_people")
        num_days = data.get("num_days")
        date_of_trip = data.get("date_of_trip")
        trip_type = data.get("trip_type")

        command = (
            f'ollama run deepseek-r1:1.5b "Generate an itinerary for {num_days} days '
            f'for {num_people} people to {destination} on {date_of_trip}. '
            f'The budget is {budget} and the trip type is {trip_type}. '
            f'Provide ONLY the itinerary, starting with \'Day 1:\' and do not include any other text, tags, or explanations."'
        )

        result = subprocess.run(command, shell=True, capture_output=True, text=True, encoding="utf-8", errors="replace", timeout=60)
        
        output = result.stdout.strip()
        cleaned_output = re.sub(r'<think>[\s\S]*?</think>', '', output)

        if result.returncode == 0:
            days = cleaned_output.split("Day ")
            itinerary_data = {}
            for day in days[1:]:
                day_number, *activities = day.split("\n")
                itinerary_data[f"Day {day_number.strip()}"] = [activity.strip() for activity in activities if activity.strip()]
            return jsonify({'itinerary': itinerary_data}), 200
        else:
            return jsonify({'error': 'Failed to generate itinerary', 'details': result.stderr}), 500

    except subprocess.TimeoutExpired:
        return jsonify({'error': 'The process timed out'}), 500
    except Exception as e:
        return jsonify({'error': str(e)}), 500

# Run the Flask app
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
    
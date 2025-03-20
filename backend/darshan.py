from flask import Flask, request, render_template, jsonify
import sqlite3
import bcrypt
from googletrans import Translator
import speech_recognition as spr
from gtts import gTTS
import os

# Initialize the Flask app
app = Flask(__name__)
translator = Translator()

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

# Function to capture and recognize speech
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

# Route for registration
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
        cursor.execute('INSERT INTO users (email, password) VALUES (?, ?)', 
                       (email, hashed_password.decode('utf-8')))
        conn.commit()
        conn.close()

        return jsonify({'message': 'User registered successfully'}), 201

    except Exception as e:
        return jsonify({'message': 'An error occurred', 'error': str(e)}), 500

# Route for login
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

# Route for text translation
@app.route('/', methods=['GET', 'POST'])
def index():
    translated_text = ""
    source_lang = 'en'
    dest_lang = 'hi'

    if request.method == 'POST':
        text_to_translate = request.form['text']
        source_lang = request.form['source_lang']
        dest_lang = request.form['dest_lang']

        if text_to_translate:
            translated = translator.translate(text_to_translate, src=source_lang, dest=dest_lang)
            translated_text = translated.text

    languages = ['en', 'hi', 'es', 'fr', 'de', 'it', 'ja', 'ko']
    return render_template('index.html', translated_text=translated_text, source_lang=source_lang, dest_lang=dest_lang, languages=languages)

# Route for voice translation
@app.route('/voice', methods=['GET'])
def voice_translation():
    recog1 = spr.Recognizer()
    mc = spr.Microphone()

    with mc as source:
        print("Speak a sentence to translate...")
        get_sentence = recognize_speech(recog1, source)

        if get_sentence:
            print(f"Phrase to be Translated: {get_sentence}")
            translated = translator.translate(get_sentence, src='en', dest='hi')
            translated_text = translated.text

            speak = gTTS(text=translated_text, lang='hi', slow=False)
            speak.save("captured_voice.mp3")

            return translated_text
        else:
            return "Unable to capture the sentence for translation."

# Run the Flask app
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)

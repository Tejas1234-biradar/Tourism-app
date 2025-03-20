# 🌍 Tourism Itinerary Generator App

An intelligent travel companion that generates personalized itineraries and offers real-time translation support. Built with **Python** on the backend and **Kotlin** for the Android app, this project leverages **DeepSeek AI** for generating travel plans and **Google Speech API** for seamless translations.

---

## ✨ Features

- **🗺️ AI-Powered Itinerary Generation**
  - Generates customized travel itineraries based on user inputs (destination, duration, preferences) using **DeepSeek AI**.
  
- **🎙️ Real-Time Voice Translation**
  - Integrates **Google Speech API** to translate speech on the go, bridging language gaps for travelers.

- **⚡ Efficient Backend**
  - Python backend handles AI integration, request processing, and delivers optimized performance.

- **📱 Android App**
  - Built in Kotlin, providing a smooth and responsive user experience for planning trips and accessing translation features.

---

## 🛠️ Tech Stack

| Layer           | Technology                     |
|-----------------|-------------------------------|
| **Frontend**    | Kotlin (Android Studio)        |
| **Backend**     | Python                         |
| **AI Services** | DeepSeek AI (Itinerary Generation) |
| **Translation** | Google Speech-to-Text & Text-to-Speech |
| **Build Tool**  | Gradle                         |
| **Version Control** | Git & GitHub               |

---

## 🚀 How It Works

1. **User Input**
   - Travelers input their destination, trip duration, interests, and other preferences through the app.

2. **Itinerary Generation**
   - The backend sends the data to **DeepSeek AI**, which returns a tailored travel itinerary covering:
     - Tourist attractions
     - Accommodation suggestions
     - Daily activity plans

3. **Real-Time Translation**
   - Users can use the **Google Speech API** feature to translate conversations in real time, improving communication in foreign locations.

---

## 🔧 Installation

### Prerequisites
- Android Studio (Kotlin setup)
- Python 3.x installed
- DeepSeek AI API access
- Google Speech API credentials

### Backend Setup
```bash
git clone https://github.com/yourusername/Tourism-App.git
cd backend
pip install -r requirements.txt
python app.py

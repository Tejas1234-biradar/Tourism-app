ollama run deepseek-r1:1.5b "Generate an itinerary for {num_days} days '
            f'for {num_people} people to {destination} on {date_of_trip}. '
            f'The budget is {budget} and the trip type is {trip_type}. '
            f'Provide ONLY the itinerary, starting with \'Day 1:\' and do not include any other text, tags, or explanations."'
        )

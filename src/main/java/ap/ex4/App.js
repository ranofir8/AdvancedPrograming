import React, { useState } from 'react';
import { View, Button, Text, StyleSheet, TextInput } from 'react-native';
import * as Linking from 'expo-linking';

const ip = 'home.savoray.com';
const port = 18020;

const App = () => {
  const [id, setId] = useState('');
  const [scores, setScores] = useState([]);
  const [errorMessage, setErrorMessage] = useState('');

  const fetchScores = async () => {
    try {
      const response = await fetch(`http://${ip}:${port}/ScrabbleBasicB_war_exploded/scrabble/android/loadScore?ID=${id}`);
      if (response.status === 404) {
        throw new Error(`Game with ID "${id}" doesn't exist in the database`);
      }
      const text = await response.text();
      let data;
      try {
        data = JSON.parse(text);
      } catch (error) {
        throw new Error(`Game with ID "${id}" doesn't exist in the database`);
      }
      const formattedScores = data.map(({ playerName, playerScore }) => ({
        playerName,
        playerScore,
      }));
      setScores(formattedScores);
      setErrorMessage('');
    } catch (error) {
      setErrorMessage('Error fetching scores: ' + error.message);
    }
  };
  

  const openWebURL = () => {
    Linking.openURL(`http://${ip}:${port}/ScrabbleBasicB_war_exploded/scrabble/android/loadScore?ID=${id}`);
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Player Scores</Text>
      <TextInput
        style={styles.input}
        placeholder="Enter ID"
        value={id}
        onChangeText={setId}
      />
      <Button title="Fetch Scores" onPress={fetchScores} />
      <Button title="Open URL in Browser" onPress={openWebURL} />
      {errorMessage ? <Text style={styles.errorText}>{errorMessage}</Text> : null}
      <View style={styles.table}>
        <View style={styles.row}>
          <Text style={styles.header}>Player</Text>
          <Text style={styles.header}>Score</Text>
        </View>
        {scores.map(({ playerName, playerScore }) => (
          <View style={styles.row} key={playerName}>
            <Text style={styles.playerName}>{playerName}</Text>
            <Text style={styles.playerScore}>{playerScore}</Text>
          </View>
        ))}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  input: {
    width: '100%',
    height: 40,
    borderColor: 'gray',
    borderWidth: 1,
    marginBottom: 10,
    paddingHorizontal: 10,
  },
  errorText: {
    color: 'red',
    marginBottom: 10,
  },
  table: {
    width: '100%',
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingHorizontal: 10,
    marginBottom: 5,
  },
  header: {
    fontSize: 18,
    fontWeight: 'bold',
    flex: 1,
    textAlign: 'center',
  },
  playerName: {
    fontSize: 18,
    flex: 1,
    textAlign: 'center',
  },
  playerScore: {
    fontSize: 18,
    flex: 1,
    textAlign: 'center',
  },
});

export default App;
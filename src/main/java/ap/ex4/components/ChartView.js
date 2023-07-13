import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

const ChartView = ({ title, data }) => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>{title}</Text>
      <Text style={styles.chartText}>{data}</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#ffffff',
    padding: 10,
    borderRadius: 5,
    marginBottom: 10,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 5,
    color: '#000000',
  },
  chartText: {
    fontSize: 16,
    color: '#000000',
  },
});

export default ChartView;

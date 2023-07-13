import React from 'react';
import { StyleSheet, Image } from 'react-native';

const ImageViewer = ({ placeholderImageSource }) => {
  return <Image source={placeholderImageSource} style={styles.image} />;
};

const styles = StyleSheet.create({
  image: {
    width: 320,
    height: 440,
    borderRadius: 18,
  },
});

export default ImageViewer;
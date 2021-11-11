import { StatusBar } from 'expo-status-bar';
import React from 'react';
import { SafeAreaView, Text } from 'react-native';
import { componentStyles } from "../styles/styleTemplate";

export default function EventList() {
    return (
        <SafeAreaView style={componentStyles.container}>
            <Text>This is a component.</Text>
            <StatusBar style="auto" />
        </SafeAreaView>
    );
}
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from flask import Flask
from sklearn.model_selection import train_test_split
from sklearn.metrics import r2_score, mean_squared_error
from sklearn.preprocessing import MinMaxScaler
from sklearn.neighbors import KNeighborsRegressor
from sklearn.linear_model import LinearRegression
from sklearn.svm import SVR
from sklearn.ensemble import RandomForestRegressor
from sklearn.tree import DecisionTreeRegressor
from scipy.stats import pearsonr
from sklearn.ensemble import GradientBoostingRegressor
from sklearn.linear_model import SGDRegressor
import statsmodels.api as sm
from sklearn.model_selection import GridSearchCV
import seaborn as sns
import flask

app = Flask(__name__)

@app.route("/")
def test():
    df = pd.read_csv('data/ADMISSIONS.csv')
    df['ADMITTIME'] = pd.to_datetime(df['ADMITTIME'])
    df['DISCHTIME'] = pd.to_datetime(df['DISCHTIME'])
    df['LOS'] = (df['DISCHTIME'] - df['ADMITTIME']).dt.total_seconds() / 86400
    df[['ADMITTIME', 'DISCHTIME', 'LOS']].head()
    df['LOS'].describe()
    df[df['LOS'] < 0]
    df['LOS'][df['LOS'] > 0].describe()
    df = df[df['LOS'] > 0]
    df.drop(columns=['DISCHTIME', 'ROW_ID',
                     'EDREGTIME', 'EDOUTTIME', 'HOSPITAL_EXPIRE_FLAG',
                     'HAS_CHARTEVENTS_DATA'], inplace=True)
    df['DECEASED'] = df['DEATHTIME'].notnull().map({True: 1, False: 0})
    return "{} of {} patients died in the hospital".format(df['DECEASED'].sum(),
                                                     df['SUBJECT_ID'].nunique())

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
from flask import Flask, request, make_response
import joblib

app = Flask(__name__)
model = joblib.load('reg_model.pkl')

@app.route("/", methods=['POST'])
def home():
    content = request.get_json()
    print(content)
    df = pd.DataFrame([content])
    test = df.drop(columns=['LOS', 'ID'])
    print(test)
    prediction = model.predict(test)
    print(prediction)
    return app.response_class(
        response=str(np.round(prediction[0],1)),
        status=200,
        mimetype='text/plain'
    )

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8000, debug=True)
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';
import { HttpClientModule } from '@angular/common/http'

import { AppComponent } from './app.component';
import { SearchComponent } from './components/search/search.component';
import { ListRecipesComponent } from './components/list-recipes/list-recipes.component';
import { MealDbService } from './services/mealdb.service';
import { RecipeComponent } from './components/recipe/recipe.component';
import { GoogleLoginProvider, FacebookLoginProvider, SocialAuthServiceConfig, SocialLoginModule } from '@abacritt/angularx-social-login';
import { CreateComponent } from './components/account/create/create.component';
import { LoginComponent } from './components/account/login/login.component';
import { AccountService } from './services/account.service';
import { ProfileComponent } from './components/profile/profile.component';
import { CreateRecipeComponent } from './components/recipe/create-recipe.component';
import { MyRecipeService } from './services/my-recipe.service';
import { NotFoundComponent } from './components/error/not-found.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './material.module';

const appRoutes : Routes = [
  {path:"", component: LoginComponent},
  {path:"profile", component: ProfileComponent},
  {path:"account/create", component: CreateComponent},
  {path:"account/recipe/create", component:CreateRecipeComponent},
  {path:"search", component:SearchComponent},
  {path:"recipes", component: ListRecipesComponent},
  {path:"recipes/category/:cat", component:ListRecipesComponent},
  {path:"recipes/area/:area", component:ListRecipesComponent},
  {path:"recipe/:user/:recipeId", component:RecipeComponent},
  {path:"recipe/:recipeId", component:RecipeComponent},
  {path:"**", component: NotFoundComponent}
]

@NgModule({
  declarations: [
    AppComponent,
    SearchComponent,
    ListRecipesComponent,
    RecipeComponent,
    CreateComponent,
    LoginComponent,
    ProfileComponent,
    CreateRecipeComponent,
    NotFoundComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forRoot(appRoutes, {useHash:true}),
    HttpClientModule,
    SocialLoginModule,
    BrowserAnimationsModule,
    MaterialModule
  ],
  providers: [MealDbService, AccountService, MyRecipeService, {
    provide: 'SocialAuthServiceConfig',
      useValue: {
        autoLogin: true,
        providers: [
          {
            id: GoogleLoginProvider.PROVIDER_ID,
            provider: new GoogleLoginProvider('942508289464-7dfn185umum2qefq9ftt7smk34p6p7gs.apps.googleusercontent.com'),
          },
          {
            id: FacebookLoginProvider.PROVIDER_ID,
            provider: new FacebookLoginProvider('648350570237297')
          }
        ],
      } as SocialAuthServiceConfig,
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }

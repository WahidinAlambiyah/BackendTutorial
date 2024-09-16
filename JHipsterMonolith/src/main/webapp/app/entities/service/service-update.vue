<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="jHipsterMonolithApp.service.home.createOrEditLabel"
          data-cy="ServiceCreateUpdateHeading"
          v-text="t$('jHipsterMonolithApp.service.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="service.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="service.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.service.name')" for="service-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="service-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.service.description')" for="service-description"></label>
            <textarea
              class="form-control"
              name="description"
              id="service-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.service.price')" for="service-price"></label>
            <input
              type="number"
              class="form-control"
              name="price"
              id="service-price"
              data-cy="price"
              :class="{ valid: !v$.price.$invalid, invalid: v$.price.$invalid }"
              v-model.number="v$.price.$model"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('jHipsterMonolithApp.service.durationInHours')"
              for="service-durationInHours"
            ></label>
            <input
              type="number"
              class="form-control"
              name="durationInHours"
              id="service-durationInHours"
              data-cy="durationInHours"
              :class="{ valid: !v$.durationInHours.$invalid, invalid: v$.durationInHours.$invalid }"
              v-model.number="v$.durationInHours.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.service.serviceType')" for="service-serviceType"></label>
            <select
              class="form-control"
              name="serviceType"
              :class="{ valid: !v$.serviceType.$invalid, invalid: v$.serviceType.$invalid }"
              v-model="v$.serviceType.$model"
              id="service-serviceType"
              data-cy="serviceType"
            >
              <option
                v-for="serviceType in serviceTypeValues"
                :key="serviceType"
                v-bind:value="serviceType"
                v-bind:label="t$('jHipsterMonolithApp.ServiceType.' + serviceType)"
              >
                {{ serviceType }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.service.testimonial')" for="service-testimonial"></label>
            <select class="form-control" id="service-testimonial" data-cy="testimonial" name="testimonial" v-model="service.testimonial">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  service.testimonial && testimonialOption.id === service.testimonial.id ? service.testimonial : testimonialOption
                "
                v-for="testimonialOption in testimonials"
                :key="testimonialOption.id"
              >
                {{ testimonialOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./service-update.component.ts"></script>
